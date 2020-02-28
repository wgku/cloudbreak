package com.sequenceiq.periscope.monitor.evaluator.services;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.auth.altus.Crn;
import com.sequenceiq.cloudbreak.client.HttpClientConfig;
import com.sequenceiq.cloudbreak.client.RestClientUtil;
import com.sequenceiq.cloudbreak.service.secret.service.SecretService;
import com.sequenceiq.cloudbreak.util.JaxRSUtil;
import com.sequenceiq.periscope.domain.Cluster;
import com.sequenceiq.periscope.model.TlsConfiguration;
import com.sequenceiq.periscope.model.clusterservices.YarnScalingResponse;
import com.sequenceiq.periscope.monitor.context.ClusterIdEvaluatorContext;
import com.sequenceiq.periscope.monitor.context.EvaluatorContext;
import com.sequenceiq.periscope.monitor.evaluator.EvaluatorExecutor;
import com.sequenceiq.periscope.monitor.evaluator.EventPublisher;
import com.sequenceiq.periscope.monitor.event.UpdateFailedEvent;
import com.sequenceiq.periscope.service.ClusterService;
import com.sequenceiq.periscope.service.configuration.CloudbreakClientConfiguration;
import com.sequenceiq.periscope.service.security.TlsHttpClientConfigurationService;
import com.sequenceiq.periscope.service.security.TlsSecurityService;
import com.sequenceiq.periscope.utils.CloudVMUtils;
import com.sequenceiq.periscope.utils.StackResponseUtils;

@Component("YarnScalingEvaluator")
@Scope("prototype")
public class YarnScalingEvaluator extends EvaluatorExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YarnScalingEvaluator.class);

    private static final String EVALUATOR_NAME = YarnScalingEvaluator.class.getName();

    private static final String AUTOSCALE_MACHINE_USER_NAME_PATTERN = "datahub-autoscale-yarn-metrics-collector-%s";

    private static final String CLUSTER_PROXY_URL_PATTERN = "%s/proxy/%s/resourcemanager/v1/cluster/scaling";

    private long clusterId;

    private EvaluatorContext context;

    @Autowired
    private ClusterService clusterService;

    @Inject
    private TlsSecurityService tlsSecurityService;

    @Inject
    private EventPublisher eventPublisher;

    @Inject
    private StackResponseUtils stackResponseUtils;

    @Inject
    private TlsHttpClientConfigurationService tlsHttpClientConfigurationService;

    @Inject
    private CloudVMUtils vmUtils;

    @Inject
    private YarnNewNMRequestHandler yarnNewNMRequestHandler;

    @Inject
    private YarnDecommissionCandidatesRequestHandler yarnDecommissionCandidatesRequestHandler;

    @Inject
    private SecretService secretService;

    @Inject
    private CloudbreakClientConfiguration cloudbreakClientConfiguration;

    @Override
    @Nonnull
    public EvaluatorContext getContext() {
        return new ClusterIdEvaluatorContext(clusterId);
    }

    @Override
    public String getName() {
        return EVALUATOR_NAME;
    }

    @Override
    public void setContext(EvaluatorContext context) {
        clusterId = (long) context.getData();
    }

    @Override
    public void execute() {
        long start = System.currentTimeMillis();
        try {
            Cluster cluster = clusterService.findById(clusterId);
            HttpClientConfig httpClientConfig = tlsHttpClientConfigurationService.buildTLSClientConfig(cluster.getStackCrn(), cluster.getHost(),
                    cluster.getTunnel());
            String yarnAPIUrl = String.format(CLUSTER_PROXY_URL_PATTERN,
                    httpClientConfig.getClusterProxyUrl(), httpClientConfig.getClusterCrn());

            TlsConfiguration tlsConfig = tlsSecurityService.getTls(cluster.getId());
            Client client = RestClientUtil.createClient(tlsConfig.getServerCert(),
                    tlsConfig.getClientCert(), tlsConfig.getClientKey(), true);

            StackV4Response stack =
                    cloudbreakClientConfiguration.cloudbreakInternalCrnClientClient().withInternalCrn().autoscaleEndpoint()
                            .get(cluster.getStackCrn());
            Integer vmMemory = vmUtils.getMemoryForVmType(stackResponseUtils
                    .getVMTypeForHostGroup(stack, "compute").orElseGet(() -> "default"));

            String machineUser = getAutoScaleMachineUserCRN(cluster);
            Response response = client.target(yarnAPIUrl)
                    .request()
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .header("x-cdp-actor-crn", machineUser)
                    .property("vmMemory", vmMemory)
                    .get();
            YarnScalingResponse yarnScalingResponse = JaxRSUtil.response(response, YarnScalingResponse.class);

            List<YarnScalingResponse.Candidates> decommissionCandidates = yarnScalingResponse.getDecommissionCandidates();
            LOGGER.debug("Decommission Candidates for cluster crn {} are {}", cluster.getStackCrn(), decommissionCandidates);
            if (decommissionCandidates.size() > 0) {
                yarnDecommissionCandidatesRequestHandler.handleDecommissionCandidates(cluster, decommissionCandidates);
            }

            List<YarnScalingResponse.NewNMCandidates> nmCandidates = yarnScalingResponse.getNewNMCandidates();
            LOGGER.debug("New NM Candidates for cluster crn {} are {}", cluster.getStackCrn(), nmCandidates);
            if (nmCandidates.size() > 0) {
                yarnNewNMRequestHandler.handleNewNMRequest(cluster, nmCandidates);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to retrieve scaling recommendations from yarn ", e);
            eventPublisher.publishEvent(new UpdateFailedEvent(clusterId));
        } finally {
            LOGGER.debug("Finished YarnScalingEvaluator for cluster {} in {} ms", clusterId, System.currentTimeMillis() - start);
        }
    }

    private String getAutoScaleMachineUserCRN(Cluster cluster) {
        return String.format(AUTOSCALE_MACHINE_USER_NAME_PATTERN,
                Crn.fromString(cluster.getStackCrn()).getResource());
    }
}

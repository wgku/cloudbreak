package com.sequenceiq.cloudbreak.cm;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cloudera.api.swagger.client.ApiClient;
import com.sequenceiq.cloudbreak.client.HttpClientConfig;
import com.sequenceiq.cloudbreak.cm.client.ClouderaManagerApiClientProvider;
import com.sequenceiq.cloudbreak.cm.client.retry.ClouderaManagerApiFactory;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessor;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessorFactory;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;

@Service
public class ClouderaManagerDeregisterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClouderaManagerDeregisterService.class);

    private static final String NIFI_REGISTRY_SERVER = "NIFI_REGISTRY_SERVER";

    private static final String NIFI_NODE = "NIFI_NODE";

    @Inject
    private ClouderaManagerApiClientProvider clouderaManagerApiClientProvider;

    @Inject
    private ClouderaManagerApiFactory clouderaManagerApiFactory;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private CmTemplateProcessorFactory cmTemplateProcessorFactory;

    public void deregisterServices(HttpClientConfig clientConfig, Stack stack) {
        Cluster cluster = stack.getCluster();
        String user = cluster.getCloudbreakAmbariUser();
        String password = cluster.getCloudbreakAmbariPassword();
        try {
            ApiClient client = clouderaManagerApiClientProvider.getClient(stack.getGatewayPort(), user, password, clientConfig);
            CmTemplateProcessor cmTemplateProcessor = cmTemplateProcessorFactory.get(stack.getCluster().getBlueprint().getBlueprintText());
            if (cmTemplateProcessor.isCMComponentExistsInBlueprint(NIFI_REGISTRY_SERVER)) {
                clouderaManagerApiFactory.getServicesResourceApi(client)
                        .serviceCommandByName(stack.getName(), "RemoveRangerRepo", NIFI_REGISTRY_SERVER);
            }
            if (cmTemplateProcessor.isCMComponentExistsInBlueprint(NIFI_NODE)) {
                clouderaManagerApiFactory.getServicesResourceApi(client)
                        .serviceCommandByName(stack.getName(), "RemoveRangerRepo", NIFI_NODE);
            }
        } catch (Exception e) {
            LOGGER.warn("Couldn't remove services. It's possible CM isn't started.", e);
        }
    }

}

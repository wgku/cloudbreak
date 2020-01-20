package com.sequenceiq.cloudbreak.reactor.handler.cluster.upgrade;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.core.cluster.ClusterManagerUpgradeService;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.upgrade.ClusterManagerUpgradeRequest;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.upgrade.ClusterManagerUpgradeSuccess;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.upgrade.ClusterUpgradeFailedEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.resource.ClusterUpgradeRequest;
import com.sequenceiq.flow.event.EventSelectorUtil;
import com.sequenceiq.flow.reactor.api.handler.EventHandler;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class ClusterManagerUpgradeHandler implements EventHandler<ClusterUpgradeRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterManagerUpgradeHandler.class);

    @Inject
    private ClusterManagerUpgradeService clusterManagerUpgradeService;

    @Inject
    private EventBus eventBus;

    @Override
    public String selector() {
        return EventSelectorUtil.selector(ClusterManagerUpgradeRequest.class);
    }

    @Override
    public void accept(Event<ClusterUpgradeRequest> event) {
        LOGGER.debug("Accepting Cluster Manager upgrade event..");
        ClusterUpgradeRequest request = event.getData();
        Selectable result;
        try {
            clusterManagerUpgradeService.upgradeCluster(request.getResourceId());
            result = new ClusterManagerUpgradeSuccess(request.getResourceId());
        } catch (Exception e) {
            LOGGER.info("Cluster Manager upgrade event failed", e);
            result = new ClusterUpgradeFailedEvent(request.getResourceId(), e);
        }
        eventBus.notify(result.selector(), new Event<>(event.getHeaders(), result));
    }
}

package com.sequenceiq.periscope.monitor.evaluator.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.periscope.domain.Cluster;
import com.sequenceiq.periscope.domain.YarnScalingAlert;
import com.sequenceiq.periscope.model.clusterservices.YarnScalingResponse;
import com.sequenceiq.periscope.monitor.evaluator.EventPublisher;
import com.sequenceiq.periscope.monitor.event.ScalingEvent;

@Component
public class YarnNewNMRequestHandler {

    @Inject
    private EventPublisher eventPublisher;

    public void handleNewNMRequest(Cluster cluster, List<YarnScalingResponse.NewNMCandidates> nmCandidates) {
        YarnScalingAlert alert = new YarnScalingAlert();
        alert.setCluster(cluster);
        eventPublisher.publishEvent(new ScalingEvent(alert));
    }
}

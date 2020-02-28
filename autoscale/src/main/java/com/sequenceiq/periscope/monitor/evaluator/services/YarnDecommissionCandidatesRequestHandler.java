package com.sequenceiq.periscope.monitor.evaluator.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.periscope.domain.Cluster;
import com.sequenceiq.periscope.model.clusterservices.YarnScalingResponse;
import com.sequenceiq.periscope.monitor.evaluator.EventPublisher;

@Component
public class YarnDecommissionCandidatesRequestHandler {

    @Inject
    private EventPublisher eventPublisher;

    public void handleDecommissionCandidates(Cluster cluster, List<YarnScalingResponse.Candidates> decommissionCandidates) {
    }
}

package com.sequenceiq.cloudbreak.core.flow2.chain;


import static com.sequenceiq.cloudbreak.core.flow2.cluster.upgrade.ClusterUpgradeEvent.CLUSTER_UPGRADE_EVENT;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;
import com.sequenceiq.cloudbreak.reactor.api.event.orchestration.ClusterRepairTriggerEvent;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.flow.core.chain.FlowEventChainFactory;

@Component
public class UpgradeDatalakeFlowEventChainFactory implements FlowEventChainFactory<StackEvent> {

    @Inject
    ClusterRepairFlowEventChainFactory clusterRepairFlowEventChainFactory;

    @Inject
    private StackService stackService;

    @Override
    public String initEvent() {
        return FlowChainTriggers.DATALAKE_CLUSTER_UPGRADE_CHAIN_TRIGGER_EVENT;
    }

    @Override
    public Queue<Selectable> createFlowTriggerEventQueue(StackEvent event) {
        Queue<Selectable> flowEventChain = new ConcurrentLinkedQueue<>();
        flowEventChain.add(new StackEvent(CLUSTER_UPGRADE_EVENT.event(), event.getResourceId()));
        flowEventChain.addAll(getRepairChain(event));
        return flowEventChain;
    }

    private Queue<Selectable> getRepairChain(StackEvent event) {
        Stack stack = stackService.getById(event.getResourceId());
        Map<String, List<String>> nodes = stack.getCluster().getHostGroups().stream()
                .map(hostGroup -> Map.entry(hostGroup.getName(), new ArrayList<>(hostGroup
                        .getInstanceGroup()
                        .getNotTerminatedInstanceMetaDataSet()
                        .stream()
                        .map(InstanceMetaData::getDiscoveryFQDN)
                        .collect(Collectors.toList()))))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return clusterRepairFlowEventChainFactory.createFlowTriggerEventQueue(new ClusterRepairTriggerEvent(stack, nodes, Boolean.FALSE));
    }
}

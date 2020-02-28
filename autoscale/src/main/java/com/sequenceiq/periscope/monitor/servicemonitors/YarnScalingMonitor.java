package com.sequenceiq.periscope.monitor.servicemonitors;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sequenceiq.periscope.api.model.ClusterState;
import com.sequenceiq.periscope.domain.Cluster;
import com.sequenceiq.periscope.monitor.ClusterMonitor;
import com.sequenceiq.periscope.monitor.MonitorUpdateRate;
import com.sequenceiq.periscope.monitor.evaluator.services.YarnScalingEvaluator;

@Component
public class YarnScalingMonitor extends ClusterMonitor {

    @Override
    public String getIdentifier() {
        return "yarn-scaling-monitor";
    }

    @Override
    public String getTriggerExpression() {
        return MonitorUpdateRate.YARN_SCALING_MONITOR_RATE_CRON;
    }

    @Override
    public Class<?> getEvaluatorType(Cluster cluster) {
        return YarnScalingEvaluator.class;
    }

    @Override
    protected List<Cluster> getMonitored() {
        return getClusterService().findAllForNode(ClusterState.RUNNING, true, getPeriscopeNodeConfig().getId());
    }
}

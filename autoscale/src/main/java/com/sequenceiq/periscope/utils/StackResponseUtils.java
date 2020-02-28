package com.sequenceiq.periscope.utils;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceMetadataType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceStatus;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.InstanceGroupV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.instancemetadata.InstanceMetaDataV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.instancegroup.template.InstanceTemplateV4Response;

@Service
public class StackResponseUtils {

    public Optional<InstanceMetaDataV4Response> getNotTerminatedPrimaryGateways(StackV4Response stackResponse) {
        return stackResponse.getInstanceGroups().stream().flatMap(ig -> ig.getMetadata().stream()).filter(
                im -> im.getInstanceType() == InstanceMetadataType.GATEWAY_PRIMARY
                        && im.getInstanceStatus() != InstanceStatus.TERMINATED
        ).findFirst();
    }

    public Optional<String> getVMTypeForHostGroup(StackV4Response stack, String policyGroup) {
        Optional<String> instanceType = stack.getInstanceGroups().stream()
                .filter(instanceGroup -> instanceGroup.getName().equalsIgnoreCase(policyGroup))
                .findFirst()
                .map(InstanceGroupV4Response::getTemplate)
                .map(InstanceTemplateV4Response::getInstanceType);
        return instanceType;
    }
}

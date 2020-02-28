package com.sequenceiq.periscope.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.json.JsonUtil;
import com.sequenceiq.cloudbreak.service.CloudbreakResourceReaderService;
import com.sequenceiq.periscope.model.clusterservices.CloudVMTypes;

@Component
public final class CloudVMUtils {

    @Inject
    private CloudbreakResourceReaderService cloudbreakResourceReaderService;

    private Map<String, Integer> cloudVmTypeMemoryInGb = new HashMap();

    @PostConstruct
    public void init() throws IOException {
        CloudVMTypes awsVms = JsonUtil.readValue(
                cloudbreakResourceReaderService.resourceDefinition("aws-vms"), CloudVMTypes.class);
        CloudVMTypes azureVms = JsonUtil.readValue(
                cloudbreakResourceReaderService.resourceDefinition("azure-vms"), CloudVMTypes.class);
        CloudVMTypes gcpVms = JsonUtil.readValue(
                cloudbreakResourceReaderService.resourceDefinition("gcp-vms"), CloudVMTypes.class);

        awsVms.getVmTypes().forEach(vm -> cloudVmTypeMemoryInGb.put(vm.getName(), vm.getMemorySize()));
        azureVms.getVmTypes().forEach(vm -> cloudVmTypeMemoryInGb.put(vm.getName(), vm.getMemorySize()));
        gcpVms.getVmTypes().forEach(vm -> cloudVmTypeMemoryInGb.put(vm.getName(), vm.getMemorySize()));
    }

    public Integer getMemoryForVmType(String vmType) {
        return cloudVmTypeMemoryInGb.get(vmType.toLowerCase());
    }
}

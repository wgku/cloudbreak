package com.sequenceiq.periscope.model.clusterservices;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudVMTypes {

    @JsonProperty("vmtypes")
    private List<CloudVMType> vmTypes;

    public List<CloudVMType> getVmTypes() {
        return vmTypes != null ? vmTypes : List.of();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CloudVMType {

        @JsonProperty("memorysize")
        private int memorySize;

        @JsonProperty("name")
        private String name;

        public String getName() {
            return name;
        }

        public Integer getMemorySize() {
            return memorySize;
        }
    }
}

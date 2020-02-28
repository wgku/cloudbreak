package com.sequenceiq.periscope.model.clusterservices;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YarnScalingResponse {

    @JsonProperty("newNMCandidates")
    private Map<String, List<NewNMCandidates>> newNMCandidates;

    @JsonProperty(value = "decommissionCandidates")
    private Map<String, List<Candidates>> decommissionCandidates;

    public List<Candidates> getDecommissionCandidates() {
        return decommissionCandidates != null ? decommissionCandidates.get("candidates") : List.of();
    }

    public List<NewNMCandidates> getNewNMCandidates() {
        return newNMCandidates != null ? newNMCandidates.get("newNMCandidates") : List.of();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewNMCandidates {

        @JsonProperty("count")
        private int count;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidates {

        @JsonProperty("amCount")
        private int amCount;

        @JsonProperty("runningAppCount")
        private int runningAppCount;

        @JsonProperty("decommissionedRemainingSecs")
        private long decommissionedRemainingSecs;

        @JsonProperty("nodeId")
        private String nodeId;

        @JsonProperty("recommendFlag")
        private boolean recommendFlag;

        @JsonProperty("nodeState")
        private String nodeState;

    }
}

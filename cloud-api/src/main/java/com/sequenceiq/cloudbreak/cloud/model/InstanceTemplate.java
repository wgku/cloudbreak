package com.sequenceiq.cloudbreak.cloud.model;

import java.util.ArrayList;
import java.util.List;

public class InstanceTemplate {

    private String flavor;
    private String groupName;
    private long privateId;
    private List<Volume> volumes;

    public InstanceTemplate(String flavor, String groupName, long privateId) {
        this.flavor = flavor;
        this.groupName = groupName;
        this.privateId = privateId;
        volumes = new ArrayList<>();
    }

    public String getFlavor() {
        return flavor;
    }

    public List<Volume> getVolumes() {
        return volumes;
    }

    public String getGroupName() {
        return groupName;
    }

    public long getPrivateId() {
        return privateId;
    }

    public void addVolume(Volume volume) {
        volumes.add(volume);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InstanceTemplate{");
        sb.append("flavor='").append(flavor).append('\'');
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", privateId=").append(privateId);
        sb.append(", volumes=").append(volumes);
        sb.append('}');
        return sb.toString();
    }
}

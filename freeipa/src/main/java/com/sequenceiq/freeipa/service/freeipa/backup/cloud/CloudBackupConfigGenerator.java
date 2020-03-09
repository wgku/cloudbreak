package com.sequenceiq.freeipa.service.freeipa.backup.cloud;

import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import com.sequenceiq.cloudbreak.telemetry.fluent.cloud.CloudStorageConfig;

public abstract class CloudBackupConfigGenerator<T extends CloudStorageConfig> {

    protected static final String CLUSTER_BACKUP_PREFIX = "cluster-backups";

    public abstract String generateBackupLocation(String location, String clusterType,
            String clusterName, String clusterId);

    String getLocationWithoutSchemePrefixes(String input, String... schemePrefixes) {
        for (String schemePrefix : schemePrefixes) {
            if (input.startsWith(schemePrefix)) {
                String[] splitted = input.split(schemePrefix);
                if (splitted.length > 1) {
                    return splitted[1];
                }
            }
        }
        return input;
    }

    String resolveBackupFolder(CloudStorageConfig cloudStorageConfig, String clusterType,
            String clusterName, String clusterId) {
        String clusterIdentifier = String.format("%s_%s", clusterName, clusterId);

        if (StringUtils.isNotEmpty(cloudStorageConfig.getFolderPrefix())) {
            return Paths.get(cloudStorageConfig.getFolderPrefix(), CLUSTER_BACKUP_PREFIX, clusterType,
                    clusterIdentifier).toString();
        } else {
            return Paths.get(CLUSTER_BACKUP_PREFIX, clusterType, clusterIdentifier).toString();
        }
    }
}

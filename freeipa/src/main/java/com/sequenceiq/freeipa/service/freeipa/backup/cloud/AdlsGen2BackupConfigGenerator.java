package com.sequenceiq.freeipa.service.freeipa.backup.cloud;

import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.exception.CloudbreakServiceException;
import com.sequenceiq.cloudbreak.telemetry.fluent.cloud.AdlsGen2Config;

@Component
public class AdlsGen2BackupConfigGenerator extends CloudBackupConfigGenerator<AdlsGen2Config> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdlsGen2BackupConfigGenerator.class);

    private static final String[] ADLS_GEN2_SCHEME_PREFIXES = {"abfs://", "abfss://"};

    private static final String AZURE_DFS_DOMAIN_SUFFIX = ".dfs.core.windows.net";

    private static final String AZURE_BLOB_STORAGE_SUFFIX = "blob.core.windows.net";

    private static final String AZURE_BLOB_STORAGE_SCHEMA = "https://";

    @Override
    public String generateBackupLocation(String location, String clusterType,
            String clusterName, String clusterId) {
        AdlsGen2Config adlsGen2Config = generateBackupConfig(location);
        String logFolder = resolveBackupFolder(adlsGen2Config, clusterType, clusterName, clusterId);
        String hostPart = String.format("%s.%s", adlsGen2Config.getAccount(), AZURE_BLOB_STORAGE_SUFFIX);
        String generatedLocation = String.format("%s%s", AZURE_BLOB_STORAGE_SCHEMA,
                Paths.get(hostPart, adlsGen2Config.getFileSystem(), logFolder));
        LOGGER.info("The following ADLS Gen2 base folder location is generated: {} (from {})",
                generatedLocation, location);
        return generatedLocation;
    }

    private AdlsGen2Config generateBackupConfig(String location) {
        if (StringUtils.isNotEmpty(location)) {
            boolean secure = location.startsWith(ADLS_GEN2_SCHEME_PREFIXES[1]);
            String locationWithoutScheme = getLocationWithoutSchemePrefixes(location, ADLS_GEN2_SCHEME_PREFIXES);
            String[] split = locationWithoutScheme.split("@");
            String[] storageWithSuffix = split[0].split("/", 2);
            String folderPrefix = storageWithSuffix.length < 2 ? "" :  "/" + storageWithSuffix[1];
            if (split.length < 2) {
                return new AdlsGen2Config(folderPrefix, storageWithSuffix[0], null, secure);
            } else {
                String[] splitByDomain = split[1].split(AZURE_DFS_DOMAIN_SUFFIX);
                String account = splitByDomain[0];
                if (splitByDomain.length > 1) {
                    String folderPrefixAfterDomain = splitByDomain[1];
                    if (StringUtils.isNoneEmpty(folderPrefix, folderPrefixAfterDomain)) {
                        throw new CloudbreakServiceException(String.format("Invalid ADLS Gen2 path: %s", location));
                    }
                    folderPrefix = StringUtils.isNotEmpty(folderPrefixAfterDomain) ? folderPrefixAfterDomain : folderPrefix;
                }
                return new AdlsGen2Config(folderPrefix, storageWithSuffix[0], account, secure);
            }
        }
        throw new CloudbreakServiceException("Storage location parameter is missing for ADLS Gen2");
    }
}

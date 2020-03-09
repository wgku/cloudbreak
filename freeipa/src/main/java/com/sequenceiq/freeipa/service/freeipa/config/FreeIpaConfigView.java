package com.sequenceiq.freeipa.service.freeipa.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.sequenceiq.cloudbreak.orchestrator.model.Node;

public class FreeIpaConfigView {

    private static final String EMPTY_CONFIG_DEFAULT = "";

    private final boolean backupEnabled;

    private final boolean monthlyFullBackUpEnabled;

    private final boolean hourlyBackUpEnabled;

    private final boolean initialFullBackupEnabled;

    private final String backupPlatform;

    private final String backupLocation;

    private final String realm;

    private final String domain;

    private final String password;

    private final String reverseZones;

    private final String adminUser;

    private final String freeipaToReplicate;

    private final Set<Object> hosts;

    private final String azureInstanceMsi;

    @SuppressWarnings("ExecutableStatementCount")
    private FreeIpaConfigView(Builder builder) {
        this.backupEnabled = builder.backupEnabled;
        this.monthlyFullBackUpEnabled = builder.monthlyFullBackUpEnabled;
        this.hourlyBackUpEnabled = builder.hourlyBackUpEnabled;
        this.initialFullBackupEnabled = builder.initialFullBackupEnabled;
        this.backupLocation = builder.backupLocation;
        this.backupPlatform = builder.backupPlatform;
        this.realm = builder.realm;
        this.domain = builder.domain;
        this.password = builder.password;
        this.reverseZones = builder.reverseZones;
        this.adminUser = builder.adminUser;
        this.freeipaToReplicate = builder.freeipaToReplicate;
        this.hosts = builder.hosts;
        this.azureInstanceMsi = builder.azureInstanceMsi;
    }

    public String getBackupLocation() {
        return backupLocation;
    }

    public String getRealm() {
        return realm;
    }

    public String getDomain() {
        return domain;
    }

    public String getPassword() {
        return password;
    }

    public String getReverseZones() {
        return reverseZones;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public String getFreeipaToReplicate() {
        return freeipaToReplicate;
    }

    public Set<Object> getHosts() {
        return hosts;
    }

    public boolean isBackupEnabled() {
        return backupEnabled;
    }

    public boolean isMonthlyFullBackUpEnabled() {
        return monthlyFullBackUpEnabled;
    }

    public boolean isHourlyBackUpEnabled() {
        return hourlyBackUpEnabled;
    }

    public boolean isInitialFullBackupEnabled() {
        return initialFullBackupEnabled;
    }

    public String getBackupPlatform() {
        return backupPlatform;
    }

    public String getAzureInstanceMsi() {
        return azureInstanceMsi;
    }

    @SuppressWarnings("ExecutableStatementCount")
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("backup_enabled", this.backupEnabled);
        map.put("backup_location", ObjectUtils.defaultIfNull(this.backupLocation, EMPTY_CONFIG_DEFAULT));
        map.put("monthly_full_backup_enabled", this.monthlyFullBackUpEnabled);
        map.put("hourly_backup_enabled", this.hourlyBackUpEnabled);
        map.put("initial_full_backup_enabled", this.initialFullBackupEnabled);
        map.put("backup_platform", ObjectUtils.defaultIfNull(this.backupPlatform, EMPTY_CONFIG_DEFAULT));
        map.put("realm", ObjectUtils.defaultIfNull(this.realm, EMPTY_CONFIG_DEFAULT));
        map.put("domain", ObjectUtils.defaultIfNull(this.domain, EMPTY_CONFIG_DEFAULT));
        map.put("password", ObjectUtils.defaultIfNull(this.password, EMPTY_CONFIG_DEFAULT));
        map.put("reverseZones", ObjectUtils.defaultIfNull(this.reverseZones, EMPTY_CONFIG_DEFAULT));
        map.put("admin_user", ObjectUtils.defaultIfNull(this.adminUser, EMPTY_CONFIG_DEFAULT));
        map.put("freeipa_to_replicate", ObjectUtils.defaultIfNull(this.freeipaToReplicate, EMPTY_CONFIG_DEFAULT));
        map.put("azure_instance_msi", ObjectUtils.defaultIfNull(this.azureInstanceMsi, EMPTY_CONFIG_DEFAULT));
        if (CollectionUtils.isNotEmpty(this.hosts)) {
            map.put("hosts", this.hosts);
        }
        return map;
    }

    public static final class Builder {

        private boolean backupEnabled;

        private boolean monthlyFullBackUpEnabled;

        private boolean hourlyBackUpEnabled;

        private boolean initialFullBackupEnabled;

        private String backupLocation;

        private String backupPlatform;

        private String realm;

        private String domain;

        private String password;

        private String reverseZones;

        private String adminUser;

        private String azureInstanceMsi;

        private String freeipaToReplicate;

        private Set<Object> hosts;

        public FreeIpaConfigView build() {
            return new FreeIpaConfigView(this);
        }

        public Builder withBackupEnabled(boolean backupEnabled) {
            this.backupEnabled = backupEnabled;
            return this;
    }

        public Builder withMonthlyFullBackupEnabled(boolean monthlyFullBackUpEnabled) {
            this.monthlyFullBackUpEnabled = monthlyFullBackUpEnabled;
            return this;
        }

        public Builder withHourlyBackupEnabled(boolean hourlyBackUpEnabled) {
            this.hourlyBackUpEnabled = hourlyBackUpEnabled;
            return this;
        }

        public Builder withInitialFullBackupEnabled(boolean initialFullBackupEnabled) {
            this.initialFullBackupEnabled = initialFullBackupEnabled;
            return this;
        }

        public Builder withBackupLocation(String backupLocation) {
            this.backupLocation = backupLocation;
            return this;
        }

        public Builder withRealm(String realm) {
            this.realm = realm;
            return this;
        }

        public Builder withDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withReverseZones(String reverseZones) {
            this.reverseZones = reverseZones;
            return this;
        }

        public Builder withAdminUser(String adminUser) {
            this.adminUser = adminUser;
            return this;
        }

        public Builder withFreeIpaToReplicate(String freeipaToReplicate) {
            this.freeipaToReplicate = freeipaToReplicate;
            return this;
        }

        public Builder withHosts(Set<Node> hosts) {
            this.hosts = hosts.stream().map(n ->
                    Map.of("ip", n.getPrivateIp(),
                            "fqdn", n.getHostname()))
                    .collect(Collectors.toSet());
            return this;
        }

        public Builder withBackupPlatform(String backupPlatform) {
            this.backupPlatform = backupPlatform;
            return this;
        }

        public Builder withAzureInstanceMsi(String azureInstanceMsi) {
            this.azureInstanceMsi = azureInstanceMsi;
            return this;
        }
    }
}

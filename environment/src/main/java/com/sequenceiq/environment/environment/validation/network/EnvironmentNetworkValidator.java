package com.sequenceiq.environment.environment.validation.network;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.validation.ValidationResult.ValidationResultBuilder;
import com.sequenceiq.environment.environment.dto.EnvironmentDto;
import com.sequenceiq.environment.network.dto.NetworkDto;

public interface EnvironmentNetworkValidator {

    Logger LOGGER = LoggerFactory.getLogger(EnvironmentNetworkValidator.class);

    void validateDuringFlow(EnvironmentDto environmentDto, NetworkDto networkDto, ValidationResultBuilder resultBuilder);

    void validateDuringRequest(NetworkDto networkDto, ValidationResultBuilder resultBuilder);

    default void validateForNetworkEdit(EnvironmentDto environmentDto, NetworkDto networkDto, ValidationResultBuilder resultBuilder) {
        LOGGER.debug("About to validate request time network parameters");
        validateDuringRequest(networkDto, resultBuilder);
        if (resultBuilder.build().hasError()) {
            LOGGER.info("Network validation has found some issues in request time parameters, hence no further validation will be executed at this point");
            return;
        }
        LOGGER.debug("About to validate network parameters which needs some provider side communication, thus execution can take a while");
        validateDuringFlow(environmentDto, networkDto, resultBuilder);
    }

    CloudPlatform getCloudPlatform();

    default String missingParamErrorMessage(String paramName, String cloudPlatform) {
        return String.format("The '%s' parameter should be specified for the '%s' environment specific network!", paramName, cloudPlatform);
    }

    default String missingParamsErrorMsg(CloudPlatform cloudPlatform) {
        return String.format("The '%s' related network parameters should be specified!", cloudPlatform);
    }

    default Set<String> getSubnetDiff(Set<String> subnets, Set<String> subnetMetaKeys) {
        Set<String> diff = new HashSet<>();
        for (String envSubnet : subnets) {
            if (!subnetMetaKeys.contains(envSubnet)) {
                diff.add(envSubnet);
            }
        }
        return diff;
    }

}

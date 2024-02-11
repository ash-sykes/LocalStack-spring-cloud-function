package com.ash.service;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@NonFinal
@ConstructorBinding
@ConfigurationProperties(prefix = "com.ash.localstack")
public class ExampleServiceProperties {

    String appName;
    String env;
    String endpoint;
    String envId;
}

package com.ash.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.ash.service.ExampleServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {

    private final ExampleServiceProperties exampleServiceProperties;

    @Bean
    public AmazonS3 s3() {
        final AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder.standard();

        // HACK: needed when working with S3 localstack
        if (Objects.equals(exampleServiceProperties.getEnv(), "local") || Objects.equals(exampleServiceProperties.getEnv(), "test")) {
            final AwsClientBuilder.EndpointConfiguration endpoint =
                    new AwsClientBuilder.EndpointConfiguration(exampleServiceProperties.getEndpoint(), Regions.US_EAST_1.getName());

            return amazonS3ClientBuilder
                    .withEndpointConfiguration(endpoint)
                    .build();
        }

        return amazonS3ClientBuilder
                .withRegion(Regions.EU_WEST_1)
                .build();
    }
}

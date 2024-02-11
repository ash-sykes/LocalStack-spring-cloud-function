package com.ash.service;

import com.amazonaws.services.s3.AmazonS3;
import com.ash.domain.ExampleEvent;
import com.ash.domain.RepoEvent;
import com.ash.mapper.RepoEventMapper;
import com.ash.repository.ExampleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExampleService {

    private final ExampleRepository exampleRepository;
    private final RepoEventMapper repoEventMapper;
    private final AmazonS3 s3;
    private final ExampleServiceProperties exampleServiceProperties;
    private final ObjectMapper mapper;
    private final Clock clock;

    public void getEvents() {
        log.debug("Entering eventStatusCountFunction");
        final List<RepoEvent> events = exampleRepository.getRepoEvents();
        final List<ExampleEvent> mappedEvents = repoEventMapper.map(events);

        log.debug("Starting to build s3 log file");
        try {
            final String bucket =
                    String.format("%s-%s-logs", exampleServiceProperties.getAppName(), exampleServiceProperties.getEnv());
            final String filename =
                    String.format("events-%s-%s.log", exampleServiceProperties.getEnvId(), clock.instant().truncatedTo(ChronoUnit.MINUTES));
            final String payload = mapper.writeValueAsString(mappedEvents);
            log.debug("Adding payload in bucket: {}, in file: {}", bucket, filename);

            s3.putObject(bucket, filename, payload);
        }
        catch (JsonProcessingException ex) {
            log.error("Failed to convert result object", ex);
            throw new RuntimeException("Failed to convert result object");
        }
    }
}

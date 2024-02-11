package com.ash.service;

import com.amazonaws.services.s3.AmazonS3;
import com.ash.mapper.RepoEventMapper;
import com.ash.domain.ExampleEvent;
import com.ash.domain.RepoEvent;
import com.ash.repository.ExampleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExampleServiceTest {

    private static final RepoEventMapper REPO_EVENT_MAPPER = new RepoEventMapper();

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/London");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2024-02-01T10:15:01.147Z"), ZONE_ID);
    private static final Instant TIMESTAMP = CLOCK.instant().truncatedTo(ChronoUnit.MINUTES);
    private static final String APP_NAME = "test-app";
    private static final String ENV = "test";
    private static final String ENV_ID = "test";
    private static final List<ExampleEvent> EVENTS = List.of(ExampleEvent.builder().detail("Hello, World!").build());

    @Mock
    private ExampleRepository repository;
    @Mock
    private AmazonS3 s3;
    @Mock
    private ExampleServiceProperties exampleServiceProperties;
    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ExampleService underTest;

    @BeforeEach
    void setup() {
        underTest = new ExampleService(
                repository,
                REPO_EVENT_MAPPER,
                s3,
                exampleServiceProperties,
                objectMapper,
                CLOCK
        );

        when(exampleServiceProperties.getAppName()).thenReturn(APP_NAME);
        when(exampleServiceProperties.getEnv()).thenReturn(ENV);
        when(exampleServiceProperties.getEnvId()).thenReturn(ENV_ID);

        when(repository.getRepoEvents()).thenReturn(
                List.of(
                        RepoEvent.builder()
                                .name("Hello")
                                .description("World")
                                .build()
                )
        );
    }

    @Test
    void shouldUploadToS3() {
        underTest.getEvents();

        verify(s3).putObject(
                String.format("%s-%s-logs", APP_NAME, ENV),
                String.format("events-%s-%s.log", ENV_ID, TIMESTAMP),
                "[{\"detail\":\"Hello, World!\"}]"
        );
    }

    @Test
    void shouldHandleErrorWhenUploadingLogfile() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(EVENTS)).thenThrow(new JsonProcessingException("error"){});

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> underTest.getEvents())
                .withMessage("Failed to convert result object");
    }
}

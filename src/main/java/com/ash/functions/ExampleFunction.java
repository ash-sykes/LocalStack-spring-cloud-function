package com.ash.functions;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.ash.service.ExampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExampleFunction implements Consumer<ScheduledEvent> {

    private final ExampleService exampleService;

    @Override
    public void accept(final ScheduledEvent event) {
        log.info("Starting service with: {}", event);
        exampleService.getEvents();
        log.info("Finished function");
    }
}


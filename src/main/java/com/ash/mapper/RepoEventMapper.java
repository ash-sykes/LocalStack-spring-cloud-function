package com.ash.mapper;

import com.ash.domain.ExampleEvent;
import com.ash.domain.RepoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepoEventMapper {

    public List<ExampleEvent> map(List<RepoEvent> events) {
            return events.stream()
                    .map(e ->
                        ExampleEvent.builder()
                                .detail(String.format("%s, %s!", e.getName(), e.getDescription()))
                                .build()
                    )
                    .collect(Collectors.toList());
    }
}

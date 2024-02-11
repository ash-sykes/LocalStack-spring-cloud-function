package com.ash.repository;

import com.ash.domain.RepoEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExampleRepository {

    public List<RepoEvent> getRepoEvents() {
        return List.of(
                RepoEvent.builder()
                        .description("description")
                        .name("name")
                        .build()
        );
    }
}

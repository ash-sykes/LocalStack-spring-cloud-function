package com.ash.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RepoEvent {
    String name;
    String description;
}

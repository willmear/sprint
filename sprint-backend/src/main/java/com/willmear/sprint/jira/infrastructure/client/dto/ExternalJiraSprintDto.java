package com.willmear.sprint.jira.infrastructure.client.dto;

import java.time.Instant;

public record ExternalJiraSprintDto(
        Long id,
        Long boardId,
        String name,
        String goal,
        String state,
        Instant startDate,
        Instant endDate,
        Instant completeDate
) {
    // TODO: Expand when real Jira payload coverage is implemented.
}

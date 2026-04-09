package com.willmear.sprint.jira.domain.model;

import java.time.Instant;
import java.util.UUID;

public record JiraSprint(
        Long externalSprintId,
        UUID workspaceId,
        UUID jiraConnectionId,
        Long externalBoardId,
        String name,
        String goal,
        String state,
        Instant startDate,
        Instant endDate,
        Instant completeDate,
        Instant syncedAt
) {
}

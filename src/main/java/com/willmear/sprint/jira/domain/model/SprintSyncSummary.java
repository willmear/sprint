package com.willmear.sprint.jira.domain.model;

import java.time.Instant;

public record SprintSyncSummary(
        Long externalSprintId,
        String sprintName,
        int issueCount,
        int commentCount,
        int changelogEventCount,
        Instant syncedAt,
        SprintSyncStatus status,
        String message
) {
}

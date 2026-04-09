package com.willmear.sprint.jira.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SyncSprintResult(
        UUID workspaceId,
        UUID jiraConnectionId,
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

package com.willmear.sprint.api.response;

import java.time.Instant;
import java.util.UUID;

public record SyncSprintResponse(
        UUID workspaceId,
        UUID jiraConnectionId,
        Long sprintId,
        String sprintName,
        int issueCount,
        int commentCount,
        int changelogEventCount,
        Instant syncedAt,
        String status,
        String message
) {
}

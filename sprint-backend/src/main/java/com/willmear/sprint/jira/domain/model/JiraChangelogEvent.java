package com.willmear.sprint.jira.domain.model;

import java.time.Instant;
import java.util.UUID;

public record JiraChangelogEvent(
        UUID workspaceId,
        String externalHistoryId,
        String issueKey,
        String fieldName,
        String fromValue,
        String toValue,
        Instant changedAt,
        String authorDisplayName
) {
}

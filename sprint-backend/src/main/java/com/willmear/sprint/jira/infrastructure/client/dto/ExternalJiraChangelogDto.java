package com.willmear.sprint.jira.infrastructure.client.dto;

import java.time.Instant;

public record ExternalJiraChangelogDto(
        String historyId,
        String issueKey,
        String fieldName,
        String fromValue,
        String toValue,
        Instant changedAt,
        ExternalJiraUserDto author
) {
    // TODO: Expand when real Jira payload coverage is implemented.
}

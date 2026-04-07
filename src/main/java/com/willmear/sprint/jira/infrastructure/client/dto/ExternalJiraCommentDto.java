package com.willmear.sprint.jira.infrastructure.client.dto;

import java.time.Instant;

public record ExternalJiraCommentDto(
        String id,
        String issueKey,
        ExternalJiraUserDto author,
        String body,
        Instant created,
        Instant updated
) {
    // TODO: Expand when real Jira payload coverage is implemented.
}

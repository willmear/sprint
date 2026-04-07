package com.willmear.sprint.jira.domain.model;

import java.time.Instant;
import java.util.UUID;

public record JiraComment(
        UUID workspaceId,
        String externalCommentId,
        String issueKey,
        String authorDisplayName,
        String body,
        Instant createdAtExternal,
        Instant updatedAtExternal
) {
}

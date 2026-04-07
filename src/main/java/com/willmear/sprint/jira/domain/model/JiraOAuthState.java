package com.willmear.sprint.jira.domain.model;

import java.time.Instant;
import java.util.UUID;

public record JiraOAuthState(
        UUID id,
        UUID workspaceId,
        UUID connectionId,
        String state,
        String redirectUri,
        Instant expiresAt,
        boolean consumed,
        Instant createdAt,
        Instant updatedAt
) {
}

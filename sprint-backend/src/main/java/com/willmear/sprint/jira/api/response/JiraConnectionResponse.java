package com.willmear.sprint.jira.api.response;

import java.time.Instant;
import java.util.UUID;

public record JiraConnectionResponse(
        UUID id,
        UUID workspaceId,
        String baseUrl,
        String authType,
        String status,
        String clientEmailOrUsername,
        String externalAccountId,
        String externalAccountDisplayName,
        Instant tokenExpiresAt,
        Instant lastTestedAt,
        Instant createdAt,
        Instant updatedAt
) {
}

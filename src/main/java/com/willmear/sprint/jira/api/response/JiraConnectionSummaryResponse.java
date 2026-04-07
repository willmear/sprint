package com.willmear.sprint.jira.api.response;

import java.time.Instant;
import java.util.UUID;

public record JiraConnectionSummaryResponse(
        UUID id,
        UUID workspaceId,
        String baseUrl,
        String authType,
        String status,
        String externalAccountDisplayName,
        Instant lastTestedAt,
        Instant createdAt
) {
}

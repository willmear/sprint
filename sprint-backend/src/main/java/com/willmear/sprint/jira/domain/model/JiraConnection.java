package com.willmear.sprint.jira.domain.model;

import java.time.Instant;
import java.util.UUID;

public record JiraConnection(
        UUID id,
        UUID workspaceId,
        String baseUrl,
        JiraAuthType authType,
        JiraConnectionStatus status,
        String clientEmailOrUsername,
        String encryptedAccessToken,
        String encryptedRefreshToken,
        Instant tokenExpiresAt,
        Instant lastTestedAt,
        String externalAccountId,
        String externalAccountDisplayName,
        String externalAccountAvatarUrl,
        Instant createdAt,
        Instant updatedAt
) {
}

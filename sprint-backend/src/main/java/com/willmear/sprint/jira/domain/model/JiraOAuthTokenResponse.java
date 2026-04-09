package com.willmear.sprint.jira.domain.model;

import java.time.Instant;

public record JiraOAuthTokenResponse(
        String accessToken,
        String refreshToken,
        Instant expiresAt
) {
}

package com.willmear.sprint.auth.domain;

import java.time.Instant;
import java.util.UUID;

public record AuthLoginState(
        UUID id,
        String state,
        String postLoginRedirectUri,
        Instant expiresAt,
        boolean consumed,
        Instant createdAt,
        Instant updatedAt
) {
}

package com.willmear.sprint.auth.domain;

import java.time.Instant;
import java.util.UUID;

public record AuthenticatedSession(
        UUID userId,
        String sessionToken,
        Instant expiresAt,
        boolean authenticated
) {
}

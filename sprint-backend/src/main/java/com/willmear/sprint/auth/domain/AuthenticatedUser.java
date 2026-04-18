package com.willmear.sprint.auth.domain;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String externalAccountId,
        String email,
        String displayName,
        String avatarUrl,
        AuthProvider authProvider,
        Instant lastLoginAt,
        Instant sessionExpiresAt,
        Set<String> roles
) {
}

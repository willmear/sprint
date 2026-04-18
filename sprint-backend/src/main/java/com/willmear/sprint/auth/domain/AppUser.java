package com.willmear.sprint.auth.domain;

import java.time.Instant;
import java.util.UUID;

public record AppUser(
        UUID id,
        String externalAccountId,
        String email,
        String displayName,
        String avatarUrl,
        AuthProvider authProvider,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
) {
}

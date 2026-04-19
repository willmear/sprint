package com.willmear.sprint.profile.api.response;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String displayName,
        String email,
        String avatarUrl,
        String authProvider,
        Instant lastLoginAt
) {
}

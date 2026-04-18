package com.willmear.sprint.auth.mapper;

import com.willmear.sprint.auth.domain.AuthenticatedSession;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AppSessionMapper {

    public AuthenticatedSession toDomain(UUID userId, String sessionToken, Instant expiresAt) {
        return new AuthenticatedSession(userId, sessionToken, expiresAt, true);
    }
}

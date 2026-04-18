package com.willmear.sprint.auth.mapper;

import com.willmear.sprint.auth.domain.AppUser;
import com.willmear.sprint.auth.domain.AuthenticatedUser;
import com.willmear.sprint.auth.entity.AppUserEntity;
import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUser toDomain(AppUserEntity entity) {
        return new AppUser(
                entity.getId(),
                entity.getExternalAccountId(),
                entity.getEmail(),
                entity.getDisplayName(),
                entity.getAvatarUrl(),
                entity.getAuthProvider(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AuthenticatedUser toAuthenticatedUser(AppUserEntity entity, Instant sessionExpiresAt) {
        return new AuthenticatedUser(
                entity.getId(),
                entity.getExternalAccountId(),
                entity.getEmail(),
                entity.getDisplayName(),
                entity.getAvatarUrl(),
                entity.getAuthProvider(),
                entity.getLastLoginAt(),
                sessionExpiresAt,
                Set.of("ROLE_USER")
        );
    }
}

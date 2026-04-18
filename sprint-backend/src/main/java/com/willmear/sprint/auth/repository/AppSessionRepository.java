package com.willmear.sprint.auth.repository;

import com.willmear.sprint.auth.entity.AppSessionEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppSessionRepository extends JpaRepository<AppSessionEntity, UUID> {

    @Query("""
            select session
            from AppSessionEntity session
            join fetch session.user
            where session.sessionTokenHash = :sessionTokenHash
              and session.expiresAt > :now
              and session.invalidatedAt is null
            """)
    Optional<AppSessionEntity> findActiveBySessionTokenHash(
            @Param("sessionTokenHash") String sessionTokenHash,
            @Param("now") Instant now
    );

    Optional<AppSessionEntity> findBySessionTokenHash(String sessionTokenHash);
}

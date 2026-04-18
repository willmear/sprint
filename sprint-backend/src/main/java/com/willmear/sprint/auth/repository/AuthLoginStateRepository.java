package com.willmear.sprint.auth.repository;

import com.willmear.sprint.auth.entity.AuthLoginStateEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthLoginStateRepository extends JpaRepository<AuthLoginStateEntity, UUID> {

    Optional<AuthLoginStateEntity> findByStateAndConsumedFalseAndExpiresAtAfter(String state, Instant now);
}

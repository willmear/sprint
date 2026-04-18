package com.willmear.sprint.auth.repository;

import com.willmear.sprint.auth.domain.AuthProvider;
import com.willmear.sprint.auth.entity.AppUserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUserEntity, UUID> {

    Optional<AppUserEntity> findByAuthProviderAndExternalAccountId(AuthProvider authProvider, String externalAccountId);
}

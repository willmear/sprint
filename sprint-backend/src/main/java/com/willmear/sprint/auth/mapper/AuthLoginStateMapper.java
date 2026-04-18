package com.willmear.sprint.auth.mapper;

import com.willmear.sprint.auth.domain.AuthLoginState;
import com.willmear.sprint.auth.entity.AuthLoginStateEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthLoginStateMapper {

    public AuthLoginState toDomain(AuthLoginStateEntity entity) {
        return new AuthLoginState(
                entity.getId(),
                entity.getState(),
                entity.getPostLoginRedirectUri(),
                entity.getExpiresAt(),
                entity.isConsumed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AuthLoginStateEntity toEntity(AuthLoginState authLoginState) {
        AuthLoginStateEntity entity = new AuthLoginStateEntity();
        entity.setId(authLoginState.id());
        entity.setState(authLoginState.state());
        entity.setPostLoginRedirectUri(authLoginState.postLoginRedirectUri());
        entity.setExpiresAt(authLoginState.expiresAt());
        entity.setConsumed(authLoginState.consumed());
        entity.setCreatedAt(authLoginState.createdAt());
        entity.setUpdatedAt(authLoginState.updatedAt());
        return entity;
    }
}

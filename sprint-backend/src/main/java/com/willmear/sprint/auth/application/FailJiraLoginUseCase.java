package com.willmear.sprint.auth.application;

import com.willmear.sprint.auth.domain.AuthLoginState;
import com.willmear.sprint.auth.mapper.AuthLoginStateMapper;
import com.willmear.sprint.auth.repository.AuthLoginStateRepository;
import com.willmear.sprint.common.exception.InvalidOAuthStateException;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FailJiraLoginUseCase {

    private final AuthLoginStateRepository authLoginStateRepository;
    private final AuthLoginStateMapper authLoginStateMapper;

    public FailJiraLoginUseCase(
            AuthLoginStateRepository authLoginStateRepository,
            AuthLoginStateMapper authLoginStateMapper
    ) {
        this.authLoginStateRepository = authLoginStateRepository;
        this.authLoginStateMapper = authLoginStateMapper;
    }

    @Transactional
    public String fail(String state) {
        AuthLoginState loginState = authLoginStateRepository.findByStateAndConsumedFalseAndExpiresAtAfter(state, Instant.now())
                .map(authLoginStateMapper::toDomain)
                .orElseThrow(() -> new InvalidOAuthStateException(state));
        authLoginStateRepository.save(authLoginStateMapper.toEntity(new AuthLoginState(
                loginState.id(),
                loginState.state(),
                loginState.postLoginRedirectUri(),
                loginState.expiresAt(),
                true,
                loginState.createdAt(),
                Instant.now()
        )));
        return loginState.postLoginRedirectUri();
    }
}

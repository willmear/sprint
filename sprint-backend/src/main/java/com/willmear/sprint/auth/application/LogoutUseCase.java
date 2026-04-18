package com.willmear.sprint.auth.application;

import com.willmear.sprint.auth.repository.AppSessionRepository;
import com.willmear.sprint.auth.security.SessionTokenHasher;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LogoutUseCase {

    private final AppSessionRepository appSessionRepository;
    private final SessionTokenHasher sessionTokenHasher;

    public LogoutUseCase(AppSessionRepository appSessionRepository, SessionTokenHasher sessionTokenHasher) {
        this.appSessionRepository = appSessionRepository;
        this.sessionTokenHasher = sessionTokenHasher;
    }

    @Transactional
    public void logout(String sessionToken) {
        if (!StringUtils.hasText(sessionToken)) {
            return;
        }
        String tokenHash = sessionTokenHasher.hash(sessionToken);
        appSessionRepository.findBySessionTokenHash(tokenHash).ifPresent(session -> {
            session.setInvalidatedAt(Instant.now());
            appSessionRepository.save(session);
        });
    }
}

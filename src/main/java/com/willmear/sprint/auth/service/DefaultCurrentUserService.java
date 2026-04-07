package com.willmear.sprint.auth.service;

import com.willmear.sprint.auth.model.AuthenticatedUser;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DefaultCurrentUserService implements CurrentUserService {

    @Override
    public Optional<AuthenticatedUser> getCurrentUser() {
        // TODO: Bridge the authenticated principal into the application model.
        return Optional.of(new AuthenticatedUser(UUID.fromString("00000000-0000-0000-0000-000000000001"), "system", Set.of("ROLE_SYSTEM")));
    }
}


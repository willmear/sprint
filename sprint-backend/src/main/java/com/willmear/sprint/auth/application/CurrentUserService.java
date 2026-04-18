package com.willmear.sprint.auth.application;

import com.willmear.sprint.auth.domain.AuthenticatedUser;
import com.willmear.sprint.common.exception.UnauthenticatedException;
import java.util.Optional;
import java.util.UUID;

public interface CurrentUserService {

    Optional<AuthenticatedUser> getCurrentUser();

    default AuthenticatedUser requireCurrentUser() {
        return getCurrentUser().orElseThrow(() -> new UnauthenticatedException("Authentication required."));
    }

    default UUID requireCurrentUserId() {
        return requireCurrentUser().userId();
    }
}

package com.willmear.sprint.auth.api;

import com.willmear.sprint.auth.domain.AuthenticatedSession;
import com.willmear.sprint.auth.domain.AuthenticatedUser;
import java.util.Optional;

public interface AuthService {

    String startJiraLogin(String redirectTo);

    Optional<String> findActiveJiraLoginRedirect(String state);

    LoginResult completeJiraLogin(String code, String state);

    String handleJiraLoginError(String state, String error, String errorDescription);

    void logout(String sessionToken);

    record LoginResult(
            AuthenticatedUser user,
            AuthenticatedSession session,
            String redirectUri
    ) {
    }
}

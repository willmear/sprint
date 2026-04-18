package com.willmear.sprint.auth.application;

import com.willmear.sprint.auth.api.AuthService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationService implements AuthService {

    private final StartLoginWithJiraUseCase startLoginWithJiraUseCase;
    private final CompleteJiraLoginUseCase completeJiraLoginUseCase;
    private final FailJiraLoginUseCase failJiraLoginUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthApplicationService(
            StartLoginWithJiraUseCase startLoginWithJiraUseCase,
            CompleteJiraLoginUseCase completeJiraLoginUseCase,
            FailJiraLoginUseCase failJiraLoginUseCase,
            LogoutUseCase logoutUseCase
    ) {
        this.startLoginWithJiraUseCase = startLoginWithJiraUseCase;
        this.completeJiraLoginUseCase = completeJiraLoginUseCase;
        this.failJiraLoginUseCase = failJiraLoginUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @Override
    public String startJiraLogin(String redirectTo) {
        return startLoginWithJiraUseCase.start(redirectTo);
    }

    @Override
    public Optional<String> findActiveJiraLoginRedirect(String state) {
        return startLoginWithJiraUseCase.findActiveRedirect(state);
    }

    @Override
    public LoginResult completeJiraLogin(String code, String state) {
        return completeJiraLoginUseCase.complete(code, state);
    }

    @Override
    public String handleJiraLoginError(String state, String error, String errorDescription) {
        return failJiraLoginUseCase.fail(state);
    }

    @Override
    public void logout(String sessionToken) {
        logoutUseCase.logout(sessionToken);
    }
}

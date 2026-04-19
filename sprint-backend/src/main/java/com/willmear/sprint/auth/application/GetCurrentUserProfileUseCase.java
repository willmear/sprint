package com.willmear.sprint.auth.application;

import com.willmear.sprint.profile.api.response.UserProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserProfileUseCase {

    private final CurrentUserService currentUserService;

    public GetCurrentUserProfileUseCase(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public UserProfileResponse get() {
        var user = currentUserService.requireCurrentUser();
        return new UserProfileResponse(
                user.userId(),
                user.displayName(),
                user.email(),
                user.avatarUrl(),
                user.authProvider().name(),
                user.lastLoginAt()
        );
    }
}

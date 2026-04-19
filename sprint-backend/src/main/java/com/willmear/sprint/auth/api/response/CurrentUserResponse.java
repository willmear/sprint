package com.willmear.sprint.auth.api.response;

import com.willmear.sprint.profile.api.response.UserProfileResponse;

public record CurrentUserResponse(
        boolean authenticated,
        UserProfileResponse user
) {
}

package com.willmear.sprint.auth.service;

import com.willmear.sprint.auth.model.AuthenticatedUser;
import java.util.Optional;

public interface CurrentUserService {

    Optional<AuthenticatedUser> getCurrentUser();
}


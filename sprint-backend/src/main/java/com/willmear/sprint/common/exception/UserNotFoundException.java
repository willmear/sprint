package com.willmear.sprint.common.exception;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(UUID userId) {
        super("User not found: " + userId);
    }
}

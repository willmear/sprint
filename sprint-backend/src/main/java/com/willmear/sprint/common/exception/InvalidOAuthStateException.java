package com.willmear.sprint.common.exception;

public class InvalidOAuthStateException extends BadRequestException {

    public InvalidOAuthStateException(String state) {
        super("Invalid or expired OAuth state: " + state);
    }
}

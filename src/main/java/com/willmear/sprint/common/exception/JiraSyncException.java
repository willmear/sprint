package com.willmear.sprint.common.exception;

public class JiraSyncException extends IntegrationException {

    public JiraSyncException(String message) {
        super(message);
    }

    public JiraSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}

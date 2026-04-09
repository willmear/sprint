package com.willmear.sprint.common.exception;

public class JiraOAuthException extends IntegrationException {

    public JiraOAuthException(String message) {
        super(message);
    }

    public JiraOAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

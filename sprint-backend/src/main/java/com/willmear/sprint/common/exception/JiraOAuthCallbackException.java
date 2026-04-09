package com.willmear.sprint.common.exception;

import org.springframework.util.StringUtils;

public class JiraOAuthCallbackException extends BadRequestException {

    public JiraOAuthCallbackException(String error, String errorDescription) {
        super(buildMessage(error, errorDescription));
    }

    private static String buildMessage(String error, String errorDescription) {
        String message = StringUtils.hasText(errorDescription) ? errorDescription : error;
        return "Jira OAuth authorization failed: " + (StringUtils.hasText(message) ? message : "Unknown error.");
    }
}

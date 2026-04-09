package com.willmear.sprint.common.exception;

public class AiPromptBuildException extends AiGenerationException {

    public AiPromptBuildException(String message) {
        super(message);
    }

    public AiPromptBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}

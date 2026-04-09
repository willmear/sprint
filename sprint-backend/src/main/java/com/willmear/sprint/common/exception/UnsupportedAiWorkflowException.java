package com.willmear.sprint.common.exception;

public class UnsupportedAiWorkflowException extends AiGenerationException {

    public UnsupportedAiWorkflowException(String workflowName) {
        super("Unsupported AI workflow: " + workflowName);
    }
}

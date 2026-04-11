package com.willmear.sprint.common.exception;

public class InvalidArtifactExportContentException extends IntegrationException {

    public InvalidArtifactExportContentException(String message) {
        super(message);
    }

    public InvalidArtifactExportContentException(String message, Throwable cause) {
        super(message, cause);
    }
}

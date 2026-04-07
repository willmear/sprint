package com.willmear.sprint.common.exception;

import java.util.UUID;

public class JobProcessingException extends IntegrationException {

    public JobProcessingException(UUID jobId, Throwable cause) {
        super("Job processing failed for job " + jobId, cause);
    }
}

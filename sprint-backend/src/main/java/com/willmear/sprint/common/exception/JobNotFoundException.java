package com.willmear.sprint.common.exception;

import java.util.UUID;

public class JobNotFoundException extends NotFoundException {

    public JobNotFoundException(UUID jobId) {
        super("Job not found: " + jobId);
    }
}

package com.willmear.sprint.common.exception;

import com.willmear.sprint.jobs.domain.JobStatus;
import java.util.UUID;

public class JobRetryNotAllowedException extends BadRequestException {

    public JobRetryNotAllowedException(UUID jobId, JobStatus status) {
        super("Retry is not allowed for job " + jobId + " with status " + status);
    }
}

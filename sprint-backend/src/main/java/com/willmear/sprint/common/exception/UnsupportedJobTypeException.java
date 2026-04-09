package com.willmear.sprint.common.exception;

import com.willmear.sprint.jobs.domain.JobType;

public class UnsupportedJobTypeException extends IntegrationException {

    public UnsupportedJobTypeException(JobType jobType) {
        super("No processor registered for job type " + jobType);
    }
}

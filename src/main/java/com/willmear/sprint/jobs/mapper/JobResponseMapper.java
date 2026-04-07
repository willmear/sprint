package com.willmear.sprint.jobs.mapper;

import com.willmear.sprint.api.response.CreateJobResponse;
import com.willmear.sprint.api.response.JobResponse;
import com.willmear.sprint.api.response.JobStatusResponse;
import com.willmear.sprint.api.response.JobSummaryResponse;
import com.willmear.sprint.jobs.domain.Job;
import org.springframework.stereotype.Component;

@Component
public class JobResponseMapper {

    public JobResponse toResponse(Job job) {
        return new JobResponse(
                job.id(),
                job.workspaceId(),
                job.jobType().name(),
                job.status().name(),
                job.queueName(),
                job.payload(),
                job.attemptCount(),
                job.maxAttempts(),
                job.availableAt(),
                job.lockedAt(),
                job.lockedBy(),
                job.startedAt(),
                job.completedAt(),
                job.failedAt(),
                job.errorMessage(),
                job.errorCode(),
                job.createdAt(),
                job.updatedAt()
        );
    }

    public JobSummaryResponse toSummaryResponse(Job job) {
        return new JobSummaryResponse(
                job.id(),
                job.workspaceId(),
                job.jobType().name(),
                job.status().name(),
                job.attemptCount(),
                job.maxAttempts(),
                job.availableAt(),
                job.createdAt(),
                job.updatedAt()
        );
    }

    public CreateJobResponse toCreateResponse(Job job) {
        return new CreateJobResponse(job.id(), job.status().name(), job.availableAt(), job.createdAt());
    }

    public JobStatusResponse toStatusResponse(Job job) {
        return new JobStatusResponse(
                job.id(),
                job.jobType().name(),
                job.status().name(),
                job.createdAt(),
                job.updatedAt()
        );
    }
}

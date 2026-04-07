package com.willmear.sprint.jobs.application;

import com.willmear.sprint.common.exception.JobRetryNotAllowedException;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RetryJobUseCase {

    private final GetJobUseCase getJobUseCase;
    private final JobRepositoryAdapter jobRepositoryAdapter;

    public RetryJobUseCase(GetJobUseCase getJobUseCase, JobRepositoryAdapter jobRepositoryAdapter) {
        this.getJobUseCase = getJobUseCase;
        this.jobRepositoryAdapter = jobRepositoryAdapter;
    }

    public Job retry(UUID jobId) {
        Job job = getJobUseCase.get(jobId);
        if (job.status() != JobStatus.FAILED) {
            throw new JobRetryNotAllowedException(jobId, job.status());
        }

        Job retriedJob = new Job(
                job.id(),
                job.workspaceId(),
                job.jobType(),
                JobStatus.PENDING,
                job.queueName(),
                job.payload(),
                job.attemptCount(),
                job.maxAttempts(),
                Instant.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                job.createdAt(),
                Instant.now()
        );
        return jobRepositoryAdapter.save(retriedJob);
    }
}

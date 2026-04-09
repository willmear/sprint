package com.willmear.sprint.jobs.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.jobs.api.JobService;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobFilter;
import com.willmear.sprint.jobs.domain.JobType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JobApplicationService implements JobService {

    private final CreateJobUseCase createJobUseCase;
    private final GetJobUseCase getJobUseCase;
    private final ListJobsUseCase listJobsUseCase;
    private final RetryJobUseCase retryJobUseCase;

    public JobApplicationService(
            CreateJobUseCase createJobUseCase,
            GetJobUseCase getJobUseCase,
            ListJobsUseCase listJobsUseCase,
            RetryJobUseCase retryJobUseCase
    ) {
        this.createJobUseCase = createJobUseCase;
        this.getJobUseCase = getJobUseCase;
        this.listJobsUseCase = listJobsUseCase;
        this.retryJobUseCase = retryJobUseCase;
    }

    @Override
    public Job createJob(UUID workspaceId, JobType jobType, JsonNode payload, Integer maxAttempts, Instant availableAt) {
        return createJobUseCase.create(workspaceId, jobType, payload, maxAttempts, availableAt);
    }

    @Override
    public Job getJob(UUID jobId) {
        return getJobUseCase.get(jobId);
    }

    @Override
    public List<Job> listJobs(JobFilter filter) {
        return listJobsUseCase.list(filter);
    }

    @Override
    public Job retryJob(UUID jobId) {
        return retryJobUseCase.retry(jobId);
    }

    @Override
    public Job create(JobType jobType, UUID referenceId) {
        return createJobUseCase.create(jobType, referenceId);
    }
}

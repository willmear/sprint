package com.willmear.sprint.jobs.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobFilter;
import com.willmear.sprint.jobs.domain.JobType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JobService {

    Job createJob(UUID workspaceId, JobType jobType, JsonNode payload, Integer maxAttempts, Instant availableAt);

    Job getJob(UUID jobId);

    List<Job> listJobs(JobFilter filter);

    Job retryJob(UUID jobId);

    Job create(JobType jobType, UUID referenceId);
}

package com.willmear.sprint.jobs.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.willmear.sprint.config.JobsProperties;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateJobUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateJobUseCase.class);

    private final JobRepositoryAdapter jobRepositoryAdapter;
    private final JobsProperties jobsProperties;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;
    private final WorkspaceService workspaceService;

    public CreateJobUseCase(
            JobRepositoryAdapter jobRepositoryAdapter,
            JobsProperties jobsProperties,
            WorkflowMetricsRecorder workflowMetricsRecorder,
            WorkspaceService workspaceService
    ) {
        this.jobRepositoryAdapter = jobRepositoryAdapter;
        this.jobsProperties = jobsProperties;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
        this.workspaceService = workspaceService;
    }

    public Job create(UUID workspaceId, JobType jobType, JsonNode payload, Integer maxAttempts, Instant availableAt) {
        if (workspaceId != null) {
            workspaceService.getWorkspace(workspaceId);
        }
        int resolvedMaxAttempts = maxAttempts != null ? maxAttempts : jobsProperties.defaultMaxAttempts();
        Instant resolvedAvailableAt = availableAt != null ? availableAt : Instant.now();
        JsonNode resolvedPayload = payload != null ? payload : JsonNodeFactory.instance.objectNode();
        Job job = jobRepositoryAdapter.create(workspaceId, jobType, jobsProperties.defaultQueueName(), resolvedPayload, resolvedMaxAttempts, resolvedAvailableAt);
        workflowMetricsRecorder.increment("jobs.created", "jobType", jobType.name());
        LOGGER.info("job.created jobId={} jobType={} workspaceId={} availableAt={}", job.id(), jobType, workspaceId, resolvedAvailableAt);
        return job;
    }

    public Job create(JobType jobType, UUID referenceId) {
        ObjectNode payload = JsonNodeFactory.instance.objectNode();
        if (referenceId != null) {
            payload.put("referenceId", referenceId.toString());
        }
        return create(null, jobType, payload, null, null);
    }
}

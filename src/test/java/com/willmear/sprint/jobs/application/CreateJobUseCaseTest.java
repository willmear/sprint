package com.willmear.sprint.jobs.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.willmear.sprint.config.JobsProperties;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateJobUseCaseTest {

    private final JobRepositoryAdapter jobRepositoryAdapter = mock(JobRepositoryAdapter.class);
    private final WorkflowMetricsRecorder metricsRecorder = new WorkflowMetricsRecorder(new SimpleMeterRegistry());
    private final JobsProperties jobsProperties = new JobsProperties(true, Duration.ofSeconds(5), 5, "worker-1", 3, "default");
    private final CreateJobUseCase useCase = new CreateJobUseCase(jobRepositoryAdapter, jobsProperties, metricsRecorder);

    @Test
    void shouldCreateJobWithDefaults() {
        UUID workspaceId = UUID.randomUUID();
        Job expected = job(workspaceId, JobType.SYNC_SPRINT, JsonNodeFactory.instance.objectNode());
        when(jobRepositoryAdapter.create(org.mockito.ArgumentMatchers.eq(workspaceId), org.mockito.ArgumentMatchers.eq(JobType.SYNC_SPRINT), org.mockito.ArgumentMatchers.eq("default"),
                org.mockito.ArgumentMatchers.any(JsonNode.class), org.mockito.ArgumentMatchers.eq(3), org.mockito.ArgumentMatchers.any(Instant.class)))
                .thenReturn(expected);

        Job created = useCase.create(workspaceId, JobType.SYNC_SPRINT, null, null, null);

        assertThat(created).isEqualTo(expected);
        assertThat(metricsRecorder).isNotNull();
        verify(jobRepositoryAdapter).create(org.mockito.ArgumentMatchers.eq(workspaceId), org.mockito.ArgumentMatchers.eq(JobType.SYNC_SPRINT),
                org.mockito.ArgumentMatchers.eq("default"), org.mockito.ArgumentMatchers.any(JsonNode.class), org.mockito.ArgumentMatchers.eq(3),
                org.mockito.ArgumentMatchers.any(Instant.class));
    }

    @Test
    void shouldCreateReferencePayloadForConvenienceMethod() {
        UUID referenceId = UUID.randomUUID();
        ArgumentCaptor<JsonNode> payloadCaptor = ArgumentCaptor.forClass(JsonNode.class);
        when(jobRepositoryAdapter.create(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.eq(JobType.GENERATE_SPRINT_REVIEW), org.mockito.ArgumentMatchers.eq("default"),
                payloadCaptor.capture(), org.mockito.ArgumentMatchers.eq(3), org.mockito.ArgumentMatchers.any(Instant.class)))
                .thenReturn(job(null, JobType.GENERATE_SPRINT_REVIEW, JsonNodeFactory.instance.objectNode()));

        useCase.create(JobType.GENERATE_SPRINT_REVIEW, referenceId);

        assertThat(payloadCaptor.getValue().get("referenceId").asText()).isEqualTo(referenceId.toString());
    }

    private Job job(UUID workspaceId, JobType jobType, JsonNode payload) {
        Instant now = Instant.now();
        return new Job(UUID.randomUUID(), workspaceId, jobType, JobStatus.PENDING, "default", payload, 0, 3, now, null, null, null, null, null, null, null, now, now);
    }
}

package com.willmear.sprint.jobs.application;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.willmear.sprint.common.exception.JobProcessingException;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobExecutionResult;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.jobs.processor.JobProcessor;
import com.willmear.sprint.jobs.processor.JobProcessorRegistry;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import com.willmear.sprint.observability.logging.LoggingContextHelper;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.support.test.ScopedLogLevel;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessJobUseCaseTest {

    private final JobProcessorRegistry jobProcessorRegistry = mock(JobProcessorRegistry.class);
    private final JobRepositoryAdapter jobRepositoryAdapter = mock(JobRepositoryAdapter.class);
    private final JobProcessor jobProcessor = mock(JobProcessor.class);
    private final WorkflowMetricsRecorder metricsRecorder = new WorkflowMetricsRecorder(new SimpleMeterRegistry());
    private final ProcessJobUseCase useCase = new ProcessJobUseCase(
            jobProcessorRegistry,
            jobRepositoryAdapter,
            new LoggingContextHelper(),
            metricsRecorder,
            new TraceContextHelper()
    );

    @Test
    void shouldMarkJobCompletedWhenProcessorSucceeds() {
        Job job = runningJob();
        when(jobProcessorRegistry.getProcessor(JobType.SYNC_SPRINT)).thenReturn(jobProcessor);
        when(jobProcessor.process(job)).thenReturn(JobExecutionResult.success("ok"));
        when(jobRepositoryAdapter.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Job result = useCase.process(job);

        assertThat(result.status()).isEqualTo(JobStatus.COMPLETED);
        assertThat(result.completedAt()).isNotNull();
        assertThat(result.lockedAt()).isNull();
        verify(jobRepositoryAdapter).save(any(Job.class));
    }

    @Test
    void shouldMarkJobFailedWhenProcessorReturnsFailureResult() {
        Job job = runningJob();
        when(jobProcessorRegistry.getProcessor(JobType.SYNC_SPRINT)).thenReturn(jobProcessor);
        when(jobProcessor.process(job)).thenReturn(JobExecutionResult.failure("bad", "E1"));
        when(jobRepositoryAdapter.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Job result = useCase.process(job);

        assertThat(result.status()).isEqualTo(JobStatus.FAILED);
        assertThat(result.errorMessage()).isEqualTo("bad");
        assertThat(result.errorCode()).isEqualTo("E1");
    }

    @Test
    void shouldWrapUnexpectedProcessorExceptions() {
        Job job = runningJob();
        when(jobProcessorRegistry.getProcessor(JobType.SYNC_SPRINT)).thenReturn(jobProcessor);
        doThrow(new IllegalStateException("boom")).when(jobProcessor).process(job);
        when(jobRepositoryAdapter.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (ScopedLogLevel ignored = ScopedLogLevel.off(ProcessJobUseCase.class)) {
            assertThatThrownBy(() -> useCase.process(job))
                    .isInstanceOf(JobProcessingException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);
        }

        verify(jobRepositoryAdapter).save(any(Job.class));
    }

    private Job runningJob() {
        Instant now = Instant.now();
        return new Job(
                UUID.randomUUID(),
                UUID.randomUUID(),
                JobType.SYNC_SPRINT,
                JobStatus.RUNNING,
                "default",
                JsonNodeFactory.instance.objectNode(),
                1,
                3,
                now,
                now,
                "worker",
                now,
                null,
                null,
                null,
                null,
                now.minusSeconds(60),
                now
        );
    }
}

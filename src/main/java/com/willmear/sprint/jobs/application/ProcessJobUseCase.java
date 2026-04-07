package com.willmear.sprint.jobs.application;

import com.willmear.sprint.common.exception.JobProcessingException;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobExecutionResult;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.processor.JobProcessor;
import com.willmear.sprint.jobs.processor.JobProcessorRegistry;
import com.willmear.sprint.jobs.repository.JobRepositoryAdapter;
import com.willmear.sprint.observability.logging.LoggingContextHelper;
import com.willmear.sprint.observability.logging.MdcKeys;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.observability.tracing.TraceNames;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessJobUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessJobUseCase.class);

    private final JobProcessorRegistry jobProcessorRegistry;
    private final JobRepositoryAdapter jobRepositoryAdapter;
    private final LoggingContextHelper loggingContextHelper;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;
    private final TraceContextHelper traceContextHelper;

    public ProcessJobUseCase(
            JobProcessorRegistry jobProcessorRegistry,
            JobRepositoryAdapter jobRepositoryAdapter,
            LoggingContextHelper loggingContextHelper,
            WorkflowMetricsRecorder workflowMetricsRecorder,
            TraceContextHelper traceContextHelper
    ) {
        this.jobProcessorRegistry = jobProcessorRegistry;
        this.jobRepositoryAdapter = jobRepositoryAdapter;
        this.loggingContextHelper = loggingContextHelper;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
        this.traceContextHelper = traceContextHelper;
    }

    public Job process(Job job) {
        long startedAt = System.nanoTime();
        try (LoggingContextHelper.Scope ignored = loggingContextHelper.putAll(Map.of(
                MdcKeys.JOB_ID, job.id(),
                MdcKeys.JOB_TYPE, job.jobType(),
                MdcKeys.WORKSPACE_ID, job.workspaceId() != null ? job.workspaceId() : ""
        )); TraceContextHelper.Scope trace = traceContextHelper.start(TraceNames.JOB_PROCESS)) {
            JobProcessor processor = jobProcessorRegistry.getProcessor(job.jobType());
            LOGGER.info("job.processing.start jobId={} jobType={} attempt={}", job.id(), job.jobType(), job.attemptCount());
            JobExecutionResult result = processor.process(job);
            if (result.success()) {
                Job completed = new Job(
                        job.id(),
                        job.workspaceId(),
                        job.jobType(),
                        JobStatus.COMPLETED,
                        job.queueName(),
                        job.payload(),
                        job.attemptCount(),
                        job.maxAttempts(),
                        job.availableAt(),
                        null,
                        null,
                        job.startedAt(),
                        Instant.now(),
                        null,
                        null,
                        null,
                        job.createdAt(),
                        Instant.now()
                );
                Job saved = jobRepositoryAdapter.save(completed);
                workflowMetricsRecorder.increment("jobs.completed", "jobType", job.jobType().name());
                workflowMetricsRecorder.recordDuration("jobs.processing.duration", System.nanoTime() - startedAt, "jobType", job.jobType().name(), "status", "completed");
                LOGGER.info("job.processing.completed jobId={} jobType={}", job.id(), job.jobType());
                trace.close("completed");
                return saved;
            }

            Job failed = failedJob(job, result.message(), result.errorCode());
            Job saved = jobRepositoryAdapter.save(failed);
            workflowMetricsRecorder.increment("jobs.failed", "jobType", job.jobType().name(), "errorCode", result.errorCode() != null ? result.errorCode() : "unknown");
            workflowMetricsRecorder.recordDuration("jobs.processing.duration", System.nanoTime() - startedAt, "jobType", job.jobType().name(), "status", "failed");
            LOGGER.warn("job.processing.failed jobId={} jobType={} message={}", job.id(), job.jobType(), result.message());
            trace.close("failed");
            return saved;
        } catch (RuntimeException exception) {
            Job failed = failedJob(job, exception.getMessage(), "PROCESSING_ERROR");
            jobRepositoryAdapter.save(failed);
            workflowMetricsRecorder.increment("jobs.failed", "jobType", job.jobType().name(), "errorCode", "PROCESSING_ERROR");
            workflowMetricsRecorder.recordDuration("jobs.processing.duration", System.nanoTime() - startedAt, "jobType", job.jobType().name(), "status", "failed");
            LOGGER.error("job.processing.exception jobId={} jobType={}", job.id(), job.jobType(), exception);
            throw new JobProcessingException(job.id(), exception);
        }
    }

    private Job failedJob(Job job, String message, String errorCode) {
        return new Job(
                job.id(),
                job.workspaceId(),
                job.jobType(),
                JobStatus.FAILED,
                job.queueName(),
                job.payload(),
                job.attemptCount(),
                job.maxAttempts(),
                job.availableAt(),
                null,
                null,
                job.startedAt(),
                null,
                Instant.now(),
                message,
                errorCode,
                job.createdAt(),
                Instant.now()
        );
    }
}

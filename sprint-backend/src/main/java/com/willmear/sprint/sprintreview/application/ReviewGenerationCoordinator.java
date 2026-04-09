package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.observability.logging.LoggingContextHelper;
import com.willmear.sprint.observability.logging.MdcKeys;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.observability.tracing.TraceNames;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.port.ArtifactWriterPort;
import com.willmear.sprint.sprintreview.domain.port.SprintReviewGenerationPort;
import com.willmear.sprint.sprintreview.domain.service.SprintReviewValidator;
import java.util.Map;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReviewGenerationCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewGenerationCoordinator.class);

    private final BuildSprintContextUseCase buildSprintContextUseCase;
    private final SprintReviewGenerationPort sprintReviewGenerationPort;
    private final SprintReviewValidator sprintReviewValidator;
    private final ArtifactWriterPort artifactWriterPort;
    private final LoggingContextHelper loggingContextHelper;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;
    private final TraceContextHelper traceContextHelper;

    public ReviewGenerationCoordinator(
            BuildSprintContextUseCase buildSprintContextUseCase,
            SprintReviewGenerationPort sprintReviewGenerationPort,
            SprintReviewValidator sprintReviewValidator,
            ArtifactWriterPort artifactWriterPort,
            LoggingContextHelper loggingContextHelper,
            WorkflowMetricsRecorder workflowMetricsRecorder,
            TraceContextHelper traceContextHelper
    ) {
        this.buildSprintContextUseCase = buildSprintContextUseCase;
        this.sprintReviewGenerationPort = sprintReviewGenerationPort;
        this.sprintReviewValidator = sprintReviewValidator;
        this.artifactWriterPort = artifactWriterPort;
        this.loggingContextHelper = loggingContextHelper;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
        this.traceContextHelper = traceContextHelper;
    }

    @Transactional
    public SprintReview generate(
            java.util.UUID workspaceId,
            Long externalSprintId,
            GenerateSprintReviewRequest request,
            String generationSource
    ) {
        long startedAt = System.nanoTime();
        try (LoggingContextHelper.Scope ignored = loggingContextHelper.putAll(Map.of(
                MdcKeys.WORKSPACE_ID, workspaceId,
                MdcKeys.SPRINT_ID, externalSprintId
        )); TraceContextHelper.Scope trace = traceContextHelper.start(TraceNames.SPRINT_REVIEW_GENERATION)) {
            LOGGER.info("sprintreview.generation.start workspaceId={} sprintId={} generationSource={}", workspaceId, externalSprintId, generationSource);
            SprintContext context = buildSprintContextUseCase.build(
                    workspaceId,
                    externalSprintId,
                    Boolean.TRUE.equals(request.includeComments()),
                    Boolean.TRUE.equals(request.includeChangelog())
            );
            SprintReview review = sprintReviewGenerationPort.generate(
                    context,
                    new com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput(
                            workspaceId,
                            externalSprintId,
                            Boolean.TRUE.equals(request.includeComments()),
                            Boolean.TRUE.equals(request.includeChangelog()),
                            request.audience(),
                            request.tone(),
                            generationSource
                    )
            );
            SprintReview validated = sprintReviewValidator.validate(review);
            artifactWriterPort.write(validated);
            workflowMetricsRecorder.increment("sprintreview.generated", "generationSource", generationSource);
            workflowMetricsRecorder.recordDuration("sprintreview.generation.duration", System.nanoTime() - startedAt, "generationSource", generationSource, "status", "completed");
            LOGGER.info("sprintreview.generation.completed workspaceId={} sprintId={} generationSource={}", workspaceId, externalSprintId, generationSource);
            trace.close("completed");
            return validated;
        } catch (RuntimeException exception) {
            workflowMetricsRecorder.increment("sprintreview.failed", "generationSource", generationSource);
            workflowMetricsRecorder.recordDuration("sprintreview.generation.duration", System.nanoTime() - startedAt, "generationSource", generationSource, "status", "failed");
            LOGGER.error("sprintreview.generation.failed workspaceId={} sprintId={} generationSource={}", workspaceId, externalSprintId, generationSource, exception);
            throw exception;
        }
    }
}

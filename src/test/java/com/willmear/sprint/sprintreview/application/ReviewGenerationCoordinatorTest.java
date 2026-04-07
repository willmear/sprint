package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.observability.logging.LoggingContextHelper;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import com.willmear.sprint.sprintreview.domain.port.ArtifactWriterPort;
import com.willmear.sprint.sprintreview.domain.port.SprintReviewGenerationPort;
import com.willmear.sprint.sprintreview.domain.service.SprintReviewValidator;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewGenerationCoordinatorTest {

    private final BuildSprintContextUseCase buildSprintContextUseCase = mock(BuildSprintContextUseCase.class);
    private final SprintReviewGenerationPort sprintReviewGenerationPort = mock(SprintReviewGenerationPort.class);
    private final SprintReviewValidator sprintReviewValidator = mock(SprintReviewValidator.class);
    private final ArtifactWriterPort artifactWriterPort = mock(ArtifactWriterPort.class);
    private final ReviewGenerationCoordinator coordinator = new ReviewGenerationCoordinator(
            buildSprintContextUseCase,
            sprintReviewGenerationPort,
            sprintReviewValidator,
            artifactWriterPort,
            new LoggingContextHelper(),
            new WorkflowMetricsRecorder(new SimpleMeterRegistry()),
            new TraceContextHelper()
    );

    @Test
    void shouldGenerateValidateAndPersistReview() {
        UUID workspaceId = UUID.randomUUID();
        SprintContext context = new SprintContext(workspaceId, UUID.randomUUID(), 11L, "Sprint 11", "Goal", "ACTIVE", Instant.now(), Instant.now(),
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), 0, 0, 0, Instant.now());
        SprintReview review = new SprintReview(UUID.randomUUID(), workspaceId, 11L, "Sprint 11",
                new SprintSummary("Title", "Overview", null, null, null), List.of(),
                List.of(new com.willmear.sprint.sprintreview.domain.model.SprintHighlight("H", "D", List.of("SPR-1"), "FEATURE")),
                List.of(), List.of(), Instant.now(), "DIRECT", "GENERATED");
        when(buildSprintContextUseCase.build(workspaceId, 11L, true, true)).thenReturn(context);
        when(sprintReviewGenerationPort.generate(any(SprintContext.class), any(SprintReviewGenerationInput.class))).thenReturn(review);
        when(sprintReviewValidator.validate(review)).thenReturn(review);

        SprintReview generated = coordinator.generate(workspaceId, 11L, new GenerateSprintReviewRequest(true, true, false, "leadership", "concise"), "DIRECT");

        assertThat(generated).isEqualTo(review);
        verify(artifactWriterPort).write(review);
    }

    @Test
    void shouldPropagateGenerationFailures() {
        UUID workspaceId = UUID.randomUUID();
        when(buildSprintContextUseCase.build(workspaceId, 11L, false, false)).thenThrow(new IllegalStateException("boom"));

        assertThatThrownBy(() -> coordinator.generate(workspaceId, 11L, new GenerateSprintReviewRequest(false, false, false, null, null), "JOB"))
                .isInstanceOf(IllegalStateException.class);
    }
}

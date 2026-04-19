package com.willmear.sprint.sprintreview.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.config.SprintReviewProperties;
import com.willmear.sprint.credits.api.CreditService;
import com.willmear.sprint.credits.domain.UserCreditSummary;
import com.willmear.sprint.jobs.api.JobService;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.workspace.application.WorkspaceAuthorizationService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SprintReviewApplicationServiceTest {

    private final GenerateSprintReviewUseCase generateSprintReviewUseCase = mock(GenerateSprintReviewUseCase.class);
    private final BuildSprintContextUseCase buildSprintContextUseCase = mock(BuildSprintContextUseCase.class);
    private final GetSprintReviewUseCase getSprintReviewUseCase = mock(GetSprintReviewUseCase.class);
    private final JobService jobService = mock(JobService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WorkspaceAuthorizationService workspaceAuthorizationService = mock(WorkspaceAuthorizationService.class);
    private final CreditService creditService = mock(CreditService.class);

    @Test
    void shouldResolveRequestDefaultsForDirectGeneration() {
        SprintReviewApplicationService service = new SprintReviewApplicationService(
                generateSprintReviewUseCase,
                buildSprintContextUseCase,
                getSprintReviewUseCase,
                jobService,
                objectMapper,
                new SprintReviewProperties(true, false, 5, 4, true),
                workspaceAuthorizationService,
                creditService
        );
        UUID workspaceId = UUID.randomUUID();
        SprintReview review = TestSprintReviewFactory.review(workspaceId, 55L, "DIRECT");
        ArgumentCaptor<GenerateSprintReviewRequest> captor = ArgumentCaptor.forClass(GenerateSprintReviewRequest.class);
        when(creditService.consumeCurrentUserGenerationCredit()).thenReturn(summary());
        when(generateSprintReviewUseCase.generate(eq(workspaceId), eq(55L), captor.capture(), eq("DIRECT"))).thenReturn(review);

        SprintReview result = service.generateReview(workspaceId, 55L, new GenerateSprintReviewRequest(null, null, null, "leadership", "concise"));

        assertThat(result).isEqualTo(review);
        assertThat(captor.getValue().includeComments()).isTrue();
        assertThat(captor.getValue().includeChangelog()).isFalse();
        assertThat(captor.getValue().forceRegenerate()).isFalse();
        verify(creditService).consumeCurrentUserGenerationCredit();
    }

    @Test
    void shouldBuildJobPayloadWithResolvedDefaults() {
        SprintReviewApplicationService service = new SprintReviewApplicationService(
                generateSprintReviewUseCase,
                buildSprintContextUseCase,
                getSprintReviewUseCase,
                jobService,
                objectMapper,
                new SprintReviewProperties(true, true, 5, 4, true),
                workspaceAuthorizationService,
                creditService
        );
        UUID workspaceId = UUID.randomUUID();
        Job job = new Job(UUID.randomUUID(), workspaceId, JobType.GENERATE_SPRINT_REVIEW, JobStatus.PENDING, "default",
                objectMapper.createObjectNode(), 0, 3, Instant.now(), null, null, null, null, null, null, null, Instant.now(), Instant.now());
        ArgumentCaptor<com.fasterxml.jackson.databind.JsonNode> payloadCaptor = ArgumentCaptor.forClass(com.fasterxml.jackson.databind.JsonNode.class);
        when(creditService.consumeCurrentUserGenerationCredit()).thenReturn(summary());
        when(jobService.createJob(eq(workspaceId), eq(JobType.GENERATE_SPRINT_REVIEW), payloadCaptor.capture(), eq(null), eq(null))).thenReturn(job);

        Job result = service.enqueueReviewGeneration(workspaceId, 55L, new GenerateSprintReviewRequest(null, false, null, "team", "direct"));

        assertThat(result).isEqualTo(job);
        assertThat(payloadCaptor.getValue().path("includeComments").asBoolean()).isTrue();
        assertThat(payloadCaptor.getValue().path("includeChangelog").asBoolean()).isFalse();
        assertThat(payloadCaptor.getValue().path("forceRegenerate").asBoolean()).isFalse();
        assertThat(payloadCaptor.getValue().path("audience").asText()).isEqualTo("team");
        verify(creditService).consumeCurrentUserGenerationCredit();
    }

    @Test
    void shouldRejectJobEnqueueWhenDisabled() {
        SprintReviewApplicationService service = new SprintReviewApplicationService(
                generateSprintReviewUseCase,
                buildSprintContextUseCase,
                getSprintReviewUseCase,
                jobService,
                objectMapper,
                new SprintReviewProperties(true, true, 5, 4, false),
                workspaceAuthorizationService,
                creditService
        );

        assertThatThrownBy(() -> service.enqueueReviewGeneration(UUID.randomUUID(), 55L, null))
                .isInstanceOf(BadRequestException.class);
        verify(creditService, never()).consumeCurrentUserGenerationCredit();
    }

    @Test
    void shouldDelegateContextAndReadRequests() {
        SprintReviewApplicationService service = new SprintReviewApplicationService(
                generateSprintReviewUseCase,
                buildSprintContextUseCase,
                getSprintReviewUseCase,
                jobService,
                objectMapper,
                new SprintReviewProperties(true, true, 5, 4, true),
                workspaceAuthorizationService,
                creditService
        );
        UUID workspaceId = UUID.randomUUID();
        SprintContext context = TestSprintReviewFactory.context(workspaceId, UUID.randomUUID(), 55L);
        SprintReview review = TestSprintReviewFactory.review(workspaceId, 55L, "DIRECT");
        when(buildSprintContextUseCase.build(workspaceId, 55L, true, false)).thenReturn(context);
        when(getSprintReviewUseCase.get(workspaceId, 55L)).thenReturn(review);

        assertThat(service.getSprintContext(workspaceId, 55L, true, false)).isEqualTo(context);
        assertThat(service.getReview(workspaceId, 55L)).isEqualTo(review);
        verify(buildSprintContextUseCase).build(workspaceId, 55L, true, false);
        verify(getSprintReviewUseCase).get(workspaceId, 55L);
    }

    private UserCreditSummary summary() {
        return new UserCreditSummary(UUID.randomUUID(), 3, 1, 2, java.time.LocalDate.now(), true);
    }
}

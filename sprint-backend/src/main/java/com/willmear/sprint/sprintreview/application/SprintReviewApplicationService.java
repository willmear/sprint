package com.willmear.sprint.sprintreview.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.config.SprintReviewProperties;
import com.willmear.sprint.credits.api.CreditService;
import com.willmear.sprint.credits.domain.UserCreditSummary;
import com.willmear.sprint.jobs.api.JobService;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.sprintreview.api.SprintReviewService;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.workspace.application.WorkspaceAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SprintReviewApplicationService implements SprintReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SprintReviewApplicationService.class);

    private final GenerateSprintReviewUseCase generateSprintReviewUseCase;
    private final BuildSprintContextUseCase buildSprintContextUseCase;
    private final GetSprintReviewUseCase getSprintReviewUseCase;
    private final JobService jobService;
    private final ObjectMapper objectMapper;
    private final SprintReviewProperties sprintReviewProperties;
    private final WorkspaceAuthorizationService workspaceAuthorizationService;
    private final CreditService creditService;

    public SprintReviewApplicationService(
            GenerateSprintReviewUseCase generateSprintReviewUseCase,
            BuildSprintContextUseCase buildSprintContextUseCase,
            GetSprintReviewUseCase getSprintReviewUseCase,
            JobService jobService,
            ObjectMapper objectMapper,
            SprintReviewProperties sprintReviewProperties,
            WorkspaceAuthorizationService workspaceAuthorizationService,
            CreditService creditService
    ) {
        this.generateSprintReviewUseCase = generateSprintReviewUseCase;
        this.buildSprintContextUseCase = buildSprintContextUseCase;
        this.getSprintReviewUseCase = getSprintReviewUseCase;
        this.jobService = jobService;
        this.objectMapper = objectMapper;
        this.sprintReviewProperties = sprintReviewProperties;
        this.workspaceAuthorizationService = workspaceAuthorizationService;
        this.creditService = creditService;
    }

    @Override
    public SprintReview generateReview(java.util.UUID workspaceId, Long externalSprintId, GenerateSprintReviewRequest request) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        consumeGenerationCredit(workspaceId, externalSprintId, "DIRECT");
        return generateSprintReviewUseCase.generate(workspaceId, externalSprintId, resolveRequest(request), "DIRECT");
    }

    @Override
    public SprintContext getSprintContext(java.util.UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeChangelog) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return buildSprintContextUseCase.build(workspaceId, externalSprintId, includeComments, includeChangelog);
    }

    @Override
    public SprintReview getReview(java.util.UUID workspaceId, Long externalSprintId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return getSprintReviewUseCase.get(workspaceId, externalSprintId);
    }

    @Override
    public Job enqueueReviewGeneration(java.util.UUID workspaceId, Long externalSprintId, GenerateSprintReviewRequest request) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        GenerateSprintReviewRequest resolvedRequest = resolveRequest(request);
        if (!sprintReviewProperties.enableJobEntrypoint()) {
            throw new BadRequestException("Sprint review job entrypoint is disabled.");
        }
        consumeGenerationCredit(workspaceId, externalSprintId, "JOB");

        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("workspaceId", workspaceId.toString());
        payload.put("externalSprintId", externalSprintId);
        payload.put("includeComments", Boolean.TRUE.equals(resolvedRequest.includeComments()));
        payload.put("includeChangelog", Boolean.TRUE.equals(resolvedRequest.includeChangelog()));
        payload.put("forceRegenerate", Boolean.TRUE.equals(resolvedRequest.forceRegenerate()));
        if (resolvedRequest.audience() != null) {
            payload.put("audience", resolvedRequest.audience());
        }
        if (resolvedRequest.tone() != null) {
            payload.put("tone", resolvedRequest.tone());
        }

        return jobService.createJob(workspaceId, JobType.GENERATE_SPRINT_REVIEW, payload, null, null);
    }

    private void consumeGenerationCredit(java.util.UUID workspaceId, Long externalSprintId, String generationSource) {
        // V1 rule: a credit is consumed when a user request enters the review generation flow,
        // including requests that enqueue a generation job.
        UserCreditSummary summary = creditService.consumeCurrentUserGenerationCredit();
        LOGGER.info(
                "sprintreview.credits.consumed workspaceId={} sprintId={} generationSource={} usedToday={} remainingToday={} dailyLimit={}",
                workspaceId,
                externalSprintId,
                generationSource,
                summary.usedToday(),
                summary.remainingToday(),
                summary.dailyLimit()
        );
    }

    private GenerateSprintReviewRequest resolveRequest(GenerateSprintReviewRequest request) {
        if (request == null) {
            return new GenerateSprintReviewRequest(
                    sprintReviewProperties.includeCommentsByDefault(),
                    sprintReviewProperties.includeChangelogByDefault(),
                    false,
                    null,
                    null
            );
        }
        return new GenerateSprintReviewRequest(
                request.includeComments() != null ? request.includeComments() : sprintReviewProperties.includeCommentsByDefault(),
                request.includeChangelog() != null ? request.includeChangelog() : sprintReviewProperties.includeChangelogByDefault(),
                request.forceRegenerate() != null ? request.forceRegenerate() : false,
                request.audience(),
                request.tone()
        );
    }
}

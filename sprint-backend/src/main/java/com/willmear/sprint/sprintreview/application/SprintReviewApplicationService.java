package com.willmear.sprint.sprintreview.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.config.SprintReviewProperties;
import com.willmear.sprint.jobs.api.JobService;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.sprintreview.api.SprintReviewService;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import org.springframework.stereotype.Service;

@Service
public class SprintReviewApplicationService implements SprintReviewService {

    private final GenerateSprintReviewUseCase generateSprintReviewUseCase;
    private final BuildSprintContextUseCase buildSprintContextUseCase;
    private final GetSprintReviewUseCase getSprintReviewUseCase;
    private final JobService jobService;
    private final ObjectMapper objectMapper;
    private final SprintReviewProperties sprintReviewProperties;

    public SprintReviewApplicationService(
            GenerateSprintReviewUseCase generateSprintReviewUseCase,
            BuildSprintContextUseCase buildSprintContextUseCase,
            GetSprintReviewUseCase getSprintReviewUseCase,
            JobService jobService,
            ObjectMapper objectMapper,
            SprintReviewProperties sprintReviewProperties
    ) {
        this.generateSprintReviewUseCase = generateSprintReviewUseCase;
        this.buildSprintContextUseCase = buildSprintContextUseCase;
        this.getSprintReviewUseCase = getSprintReviewUseCase;
        this.jobService = jobService;
        this.objectMapper = objectMapper;
        this.sprintReviewProperties = sprintReviewProperties;
    }

    @Override
    public SprintReview generateReview(java.util.UUID workspaceId, Long externalSprintId, GenerateSprintReviewRequest request) {
        return generateSprintReviewUseCase.generate(workspaceId, externalSprintId, resolveRequest(request), "DIRECT");
    }

    @Override
    public SprintContext getSprintContext(java.util.UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeChangelog) {
        return buildSprintContextUseCase.build(workspaceId, externalSprintId, includeComments, includeChangelog);
    }

    @Override
    public SprintReview getReview(java.util.UUID workspaceId, Long externalSprintId) {
        return getSprintReviewUseCase.get(workspaceId, externalSprintId);
    }

    @Override
    public Job enqueueReviewGeneration(java.util.UUID workspaceId, Long externalSprintId, GenerateSprintReviewRequest request) {
        GenerateSprintReviewRequest resolvedRequest = resolveRequest(request);
        if (!sprintReviewProperties.enableJobEntrypoint()) {
            throw new BadRequestException("Sprint review job entrypoint is disabled.");
        }

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

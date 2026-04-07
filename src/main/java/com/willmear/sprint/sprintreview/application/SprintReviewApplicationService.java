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
        return generateSprintReviewUseCase.generate(workspaceId, externalSprintId, request, "DIRECT");
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
        if (!sprintReviewProperties.enableJobEntrypoint()) {
            throw new BadRequestException("Sprint review job entrypoint is disabled.");
        }

        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("workspaceId", workspaceId.toString());
        payload.put("externalSprintId", externalSprintId);
        payload.put("includeComments", request.includeComments());
        payload.put("includeChangelog", request.includeChangelog());
        payload.put("forceRegenerate", request.forceRegenerate());
        if (request.audience() != null) {
            payload.put("audience", request.audience());
        }
        if (request.tone() != null) {
            payload.put("tone", request.tone());
        }

        return jobService.createJob(workspaceId, JobType.GENERATE_SPRINT_REVIEW, payload, null, null);
    }
}

package com.willmear.sprint.jobs.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobExecutionResult;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.sprintreview.application.GenerateSprintReviewUseCase;
import com.willmear.sprint.sprintreview.application.SprintReviewJobPayload;
import org.springframework.stereotype.Component;

@Component
public class GenerateSprintReviewJobProcessor implements JobProcessor {

    private final GenerateSprintReviewUseCase generateSprintReviewUseCase;
    private final ObjectMapper objectMapper;

    public GenerateSprintReviewJobProcessor(
            GenerateSprintReviewUseCase generateSprintReviewUseCase,
            ObjectMapper objectMapper
    ) {
        this.generateSprintReviewUseCase = generateSprintReviewUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType supports() {
        return JobType.GENERATE_SPRINT_REVIEW;
    }

    @Override
    public JobExecutionResult process(Job job) {
        SprintReviewJobPayload payload = objectMapper.convertValue(job.payload(), SprintReviewJobPayload.class);
        generateSprintReviewUseCase.generate(
                payload.workspaceId(),
                payload.externalSprintId(),
                new GenerateSprintReviewRequest(
                        payload.includeComments(),
                        payload.includeChangelog(),
                        true,
                        payload.audience(),
                        payload.tone()
                ),
                "JOB"
        );
        // TODO: Attach artifact identifiers to job metadata once job result persistence is added.
        return JobExecutionResult.success("GENERATE_SPRINT_REVIEW orchestration completed and artifact persisted.");
    }
}

package com.willmear.sprint.sprintreview.api;

import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.UUID;

public interface SprintReviewService {

    SprintReview generateReview(UUID workspaceId, Long externalSprintId, GenerateSprintReviewRequest request);

    SprintContext getSprintContext(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeChangelog);

    SprintReview getReview(UUID workspaceId, Long externalSprintId);

    Job enqueueReviewGeneration(UUID workspaceId, Long externalSprintId, GenerateSprintReviewRequest request);
}

package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.common.exception.SprintReviewNotAvailableException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import org.springframework.stereotype.Service;

@Service
public class GenerateSprintReviewUseCase {

    private final ReviewGenerationCoordinator reviewGenerationCoordinator;
    private final GetSprintReviewUseCase getSprintReviewUseCase;

    public GenerateSprintReviewUseCase(
            ReviewGenerationCoordinator reviewGenerationCoordinator,
            GetSprintReviewUseCase getSprintReviewUseCase
    ) {
        this.reviewGenerationCoordinator = reviewGenerationCoordinator;
        this.getSprintReviewUseCase = getSprintReviewUseCase;
    }

    public SprintReview generate(
            java.util.UUID workspaceId,
            Long externalSprintId,
            GenerateSprintReviewRequest request,
            String generationSource
    ) {
        if (!Boolean.TRUE.equals(request.forceRegenerate())) {
            try {
                return getSprintReviewUseCase.get(workspaceId, externalSprintId);
            } catch (SprintReviewNotAvailableException exception) {
                // No persisted review exists yet; continue with generation.
            }
        }
        return reviewGenerationCoordinator.generate(workspaceId, externalSprintId, request, generationSource);
    }
}

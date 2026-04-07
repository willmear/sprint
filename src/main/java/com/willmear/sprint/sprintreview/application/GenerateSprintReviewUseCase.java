package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import org.springframework.stereotype.Service;

@Service
public class GenerateSprintReviewUseCase {

    private final ReviewGenerationCoordinator reviewGenerationCoordinator;

    public GenerateSprintReviewUseCase(ReviewGenerationCoordinator reviewGenerationCoordinator) {
        this.reviewGenerationCoordinator = reviewGenerationCoordinator;
    }

    public SprintReview generate(
            java.util.UUID workspaceId,
            Long externalSprintId,
            GenerateSprintReviewRequest request,
            String generationSource
    ) {
        return reviewGenerationCoordinator.generate(workspaceId, externalSprintId, request, generationSource);
    }
}

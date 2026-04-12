package com.willmear.sprint.presentationplan.application;

import com.willmear.sprint.presentationplan.api.PresentationPlanService;
import com.willmear.sprint.presentationplan.domain.PresentationPlan;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PresentationPlanApplicationService implements PresentationPlanService {

    private final CreatePresentationPlanFromSprintReviewUseCase createPresentationPlanFromSprintReviewUseCase;

    public PresentationPlanApplicationService(CreatePresentationPlanFromSprintReviewUseCase createPresentationPlanFromSprintReviewUseCase) {
        this.createPresentationPlanFromSprintReviewUseCase = createPresentationPlanFromSprintReviewUseCase;
    }

    @Override
    public PresentationPlan createPlan(UUID workspaceId, String referenceType, String referenceId, SprintReview sprintReview) {
        return createPresentationPlanFromSprintReviewUseCase.create(workspaceId, referenceType, referenceId, sprintReview);
    }
}

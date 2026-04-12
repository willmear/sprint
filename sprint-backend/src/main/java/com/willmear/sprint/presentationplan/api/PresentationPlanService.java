package com.willmear.sprint.presentationplan.api;

import com.willmear.sprint.presentationplan.domain.PresentationPlan;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.UUID;

public interface PresentationPlanService {

    PresentationPlan createPlan(UUID workspaceId, String referenceType, String referenceId, SprintReview sprintReview);
}

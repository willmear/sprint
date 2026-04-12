package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentationplan.api.PresentationPlanService;
import com.willmear.sprint.presentationplan.mapper.PresentationPlanToPresentationDeckMapper;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewToPresentationDeckMapper {

    public static final String SPRINT_REFERENCE_TYPE = "SPRINT";

    private final PresentationPlanService presentationPlanService;
    private final PresentationPlanToPresentationDeckMapper presentationPlanToPresentationDeckMapper;

    public SprintReviewToPresentationDeckMapper(
            PresentationPlanService presentationPlanService,
            PresentationPlanToPresentationDeckMapper presentationPlanToPresentationDeckMapper
    ) {
        this.presentationPlanService = presentationPlanService;
        this.presentationPlanToPresentationDeckMapper = presentationPlanToPresentationDeckMapper;
    }

    public PresentationDeck toDeck(Artifact artifact, SprintReview review) {
        return toDeck(artifact, review, null);
    }

    public PresentationDeck toDeck(Artifact artifact, SprintReview review, String themeId) {
        var plan = presentationPlanService.createPlan(
                review.workspaceId(),
                SPRINT_REFERENCE_TYPE,
                review.externalSprintId().toString(),
                review
        );
        return presentationPlanToPresentationDeckMapper.toDeck(artifact, plan, themeId);
    }
}

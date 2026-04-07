package com.willmear.sprint.sprintreview.domain.service;

import com.willmear.sprint.common.exception.SprintReviewGenerationException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewValidator {

    public SprintReview validate(SprintReview sprintReview) {
        if (sprintReview.summary() == null || sprintReview.summary().overview() == null || sprintReview.summary().overview().isBlank()) {
            throw new SprintReviewGenerationException("Generated sprint review is missing a summary overview.");
        }
        if (sprintReview.highlights() == null || sprintReview.highlights().isEmpty()) {
            throw new SprintReviewGenerationException("Generated sprint review does not contain any highlights.");
        }
        return sprintReview;
    }
}

package com.willmear.sprint.sprintreview.domain.port;

import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput;

public interface SprintReviewGenerationPort {

    SprintReview generate(SprintContext context, SprintReviewGenerationInput input);
}

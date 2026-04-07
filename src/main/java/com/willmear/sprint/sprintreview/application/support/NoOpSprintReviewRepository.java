package com.willmear.sprint.sprintreview.application.support;

import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.port.SprintReviewRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class NoOpSprintReviewRepository implements SprintReviewRepositoryPort {

    @Override
    public Optional<SprintReview> findByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId) {
        return Optional.empty();
    }

    @Override
    public SprintReview save(SprintReview sprintReview) {
        // TODO: Replace with artifact-backed persistence in the next step.
        return sprintReview;
    }
}

package com.willmear.sprint.sprintreview.domain.port;

import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.Optional;
import java.util.UUID;

public interface SprintReviewRepositoryPort {

    Optional<SprintReview> findByWorkspaceIdAndExternalSprintId(UUID workspaceId, Long externalSprintId);

    SprintReview save(SprintReview sprintReview);
}

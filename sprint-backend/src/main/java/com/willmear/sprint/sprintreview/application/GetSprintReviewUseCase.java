package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.artifact.application.GetLatestArtifactUseCase;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.common.exception.LatestArtifactNotFoundException;
import com.willmear.sprint.common.exception.SprintReviewNotAvailableException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetSprintReviewUseCase {

    private final GetLatestArtifactUseCase getLatestArtifactUseCase;
    private final SprintReviewArtifactMapper sprintReviewArtifactMapper;

    public GetSprintReviewUseCase(
            GetLatestArtifactUseCase getLatestArtifactUseCase,
            SprintReviewArtifactMapper sprintReviewArtifactMapper
    ) {
        this.getLatestArtifactUseCase = getLatestArtifactUseCase;
        this.sprintReviewArtifactMapper = sprintReviewArtifactMapper;
    }

    public SprintReview get(UUID workspaceId, Long externalSprintId) {
        try {
            return sprintReviewArtifactMapper.toSprintReview(
                    getLatestArtifactUseCase.get(
                            workspaceId,
                            ArtifactType.SPRINT_REVIEW,
                            SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE,
                            externalSprintId.toString()
                    )
            );
        } catch (LatestArtifactNotFoundException exception) {
            throw new SprintReviewNotAvailableException(workspaceId, externalSprintId);
        }
    }
}

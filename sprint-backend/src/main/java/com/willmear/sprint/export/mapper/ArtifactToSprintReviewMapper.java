package com.willmear.sprint.export.mapper;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.common.exception.ArtifactPersistenceException;
import com.willmear.sprint.common.exception.InvalidArtifactExportContentException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import org.springframework.stereotype.Component;

@Component
public class ArtifactToSprintReviewMapper {

    private final SprintReviewArtifactMapper sprintReviewArtifactMapper;

    public ArtifactToSprintReviewMapper(SprintReviewArtifactMapper sprintReviewArtifactMapper) {
        this.sprintReviewArtifactMapper = sprintReviewArtifactMapper;
    }

    public SprintReview toSprintReview(Artifact artifact) {
        if (artifact.artifactType() != ArtifactType.SPRINT_REVIEW) {
            throw new InvalidArtifactExportContentException(
                    "Artifact " + artifact.id() + " is not a sprint review artifact and cannot be exported."
            );
        }
        try {
            return sprintReviewArtifactMapper.toSprintReview(artifact);
        } catch (ArtifactPersistenceException exception) {
            throw new InvalidArtifactExportContentException(
                    "Artifact " + artifact.id() + " could not be converted into a sprint review export payload.",
                    exception
            );
        }
    }
}

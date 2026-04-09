package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.artifact.application.GetLatestArtifactUseCase;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactStatus;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.common.exception.LatestArtifactNotFoundException;
import com.willmear.sprint.common.exception.SprintReviewNotAvailableException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSprintReviewUseCaseTest {

    private final GetLatestArtifactUseCase getLatestArtifactUseCase = mock(GetLatestArtifactUseCase.class);
    private final SprintReviewArtifactMapper sprintReviewArtifactMapper = mock(SprintReviewArtifactMapper.class);
    private final GetSprintReviewUseCase useCase = new GetSprintReviewUseCase(getLatestArtifactUseCase, sprintReviewArtifactMapper);

    @Test
    void shouldMapLatestArtifactToSprintReview() {
        UUID workspaceId = UUID.randomUUID();
        Artifact artifact = new Artifact(
                UUID.randomUUID(),
                workspaceId,
                ArtifactType.SPRINT_REVIEW,
                ArtifactStatus.GENERATED,
                SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE,
                "88",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
        SprintReview review = TestSprintReviewFactory.review(workspaceId, 88L, "DIRECT");
        when(getLatestArtifactUseCase.get(workspaceId, ArtifactType.SPRINT_REVIEW, SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE, "88"))
                .thenReturn(artifact);
        when(sprintReviewArtifactMapper.toSprintReview(artifact)).thenReturn(review);

        SprintReview result = useCase.get(workspaceId, 88L);

        assertThat(result).isEqualTo(review);
    }

    @Test
    void shouldWrapMissingArtifactAsSprintReviewNotAvailable() {
        UUID workspaceId = UUID.randomUUID();
        when(getLatestArtifactUseCase.get(workspaceId, ArtifactType.SPRINT_REVIEW, SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE, "88"))
                .thenThrow(new LatestArtifactNotFoundException(workspaceId, ArtifactType.SPRINT_REVIEW, SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE, "88"));

        assertThatThrownBy(() -> useCase.get(workspaceId, 88L))
                .isInstanceOf(SprintReviewNotAvailableException.class);
    }
}

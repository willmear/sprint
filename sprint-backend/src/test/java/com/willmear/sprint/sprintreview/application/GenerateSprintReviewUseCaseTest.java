package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.common.exception.SprintReviewNotAvailableException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenerateSprintReviewUseCaseTest {

    private final ReviewGenerationCoordinator reviewGenerationCoordinator = mock(ReviewGenerationCoordinator.class);
    private final GetSprintReviewUseCase getSprintReviewUseCase = mock(GetSprintReviewUseCase.class);
    private final GenerateSprintReviewUseCase useCase = new GenerateSprintReviewUseCase(reviewGenerationCoordinator, getSprintReviewUseCase);

    @Test
    void shouldReturnPersistedReviewWhenForceRegenerateIsFalse() {
        UUID workspaceId = UUID.randomUUID();
        SprintReview persisted = TestSprintReviewFactory.review(workspaceId, 17L, "DIRECT");
        when(getSprintReviewUseCase.get(workspaceId, 17L)).thenReturn(persisted);

        SprintReview result = useCase.generate(workspaceId, 17L, new GenerateSprintReviewRequest(true, true, false, null, null), "DIRECT");

        assertThat(result).isEqualTo(persisted);
        verify(getSprintReviewUseCase).get(workspaceId, 17L);
    }

    @Test
    void shouldGenerateWhenNoPersistedReviewExists() {
        UUID workspaceId = UUID.randomUUID();
        GenerateSprintReviewRequest request = new GenerateSprintReviewRequest(true, true, false, "leadership", "concise");
        SprintReview generated = TestSprintReviewFactory.review(workspaceId, 17L, "DIRECT");
        when(getSprintReviewUseCase.get(workspaceId, 17L)).thenThrow(new SprintReviewNotAvailableException(workspaceId, 17L));
        when(reviewGenerationCoordinator.generate(workspaceId, 17L, request, "DIRECT")).thenReturn(generated);

        SprintReview result = useCase.generate(workspaceId, 17L, request, "DIRECT");

        assertThat(result).isEqualTo(generated);
        verify(reviewGenerationCoordinator).generate(workspaceId, 17L, request, "DIRECT");
    }

    @Test
    void shouldBypassPersistedLookupWhenForceRegenerateIsTrue() {
        UUID workspaceId = UUID.randomUUID();
        GenerateSprintReviewRequest request = new GenerateSprintReviewRequest(true, true, true, "leadership", "concise");
        SprintReview generated = TestSprintReviewFactory.review(workspaceId, 17L, "JOB");
        when(reviewGenerationCoordinator.generate(workspaceId, 17L, request, "JOB")).thenReturn(generated);

        SprintReview result = useCase.generate(workspaceId, 17L, request, "JOB");

        assertThat(result).isEqualTo(generated);
        verify(reviewGenerationCoordinator).generate(workspaceId, 17L, request, "JOB");
    }
}

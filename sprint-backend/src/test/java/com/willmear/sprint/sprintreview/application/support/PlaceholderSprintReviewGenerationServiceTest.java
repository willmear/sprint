package com.willmear.sprint.sprintreview.application.support;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.SprintReviewProperties;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.service.SprintThemeExtractor;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceholderSprintReviewGenerationServiceTest {

    @Test
    void shouldGenerateDeterministicFallbackReview() {
        SprintThemeExtractor sprintThemeExtractor = mock(SprintThemeExtractor.class);
        PlaceholderSprintReviewGenerationService service = new PlaceholderSprintReviewGenerationService(
                sprintThemeExtractor,
                new SprintReviewProperties(true, true, 2, 3, true)
        );
        var context = TestSprintReviewFactory.contextWithIssues();
        when(sprintThemeExtractor.extract(context, 3)).thenReturn(List.of(TestSprintReviewFactory.theme()));

        SprintReview review = service.generate(context, TestSprintReviewFactory.input("DIRECT"));

        assertThat(review.summary().title()).isEqualTo("Sprint Review: " + context.sprintName());
        assertThat(review.highlights()).hasSize(2);
        assertThat(review.blockers()).hasSize(1);
        assertThat(review.speakerNotes()).hasSize(5);
        verify(sprintThemeExtractor).extract(context, 3);
    }

    @Test
    void shouldRenderFallbackStringsForBlankAudienceAndTone() {
        SprintThemeExtractor sprintThemeExtractor = mock(SprintThemeExtractor.class);
        PlaceholderSprintReviewGenerationService service = new PlaceholderSprintReviewGenerationService(
                sprintThemeExtractor,
                new SprintReviewProperties(true, true, 2, 3, true)
        );
        var context = TestSprintReviewFactory.contextWithIssues();
        when(sprintThemeExtractor.extract(context, 3)).thenReturn(List.of());

        var notes = service.buildSpeakerNotes(context, TestSprintReviewFactory.input(""));

        assertThat(notes.getLast().note()).contains("audience=default", "tone=default");
    }
}

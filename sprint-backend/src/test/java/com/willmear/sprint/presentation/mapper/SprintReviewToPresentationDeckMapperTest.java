package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.presentation.domain.SlideType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SprintReviewToPresentationDeckMapperTest {

    private final SprintReviewToPresentationDeckMapper mapper = new SprintReviewToPresentationDeckMapper();

    @Test
    void shouldCreateStructuredEditableDeckFromSprintReview() {
        var artifact = TestSprintReviewFactory.artifact();
        var review = TestSprintReviewFactory.reviewWithHighlight();

        var deck = mapper.toDeck(artifact, review);

        assertThat(deck.referenceId()).isEqualTo(String.valueOf(review.externalSprintId()));
        assertThat(deck.slides()).hasSize(6);
        assertThat(deck.slides().get(0).slideType()).isEqualTo(SlideType.TITLE);
        assertThat(deck.slides().get(2).title()).isEqualTo("Key themes");
    }
}

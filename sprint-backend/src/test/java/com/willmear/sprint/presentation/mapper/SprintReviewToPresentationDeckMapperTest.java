package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.template.SlideTemplateType;
import com.willmear.sprint.presentation.template.DeckLayoutEngine;
import com.willmear.sprint.presentation.template.SlideTemplateRegistry;
import com.willmear.sprint.presentation.theme.application.PresentationThemeApplicationService;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import com.willmear.sprint.presentationplan.application.CreatePresentationPlanFromSprintReviewUseCase;
import com.willmear.sprint.presentationplan.application.GeneratePresentationPlanUseCase;
import com.willmear.sprint.presentationplan.application.PresentationPlanApplicationService;
import com.willmear.sprint.presentationplan.mapper.PresentationPlanToPresentationDeckMapper;
import com.willmear.sprint.presentationplan.mapper.SprintReviewToPresentationPlanMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;

class SprintReviewToPresentationDeckMapperTest {

    private final SprintReviewToPresentationDeckMapper mapper = new SprintReviewToPresentationDeckMapper(
            new PresentationPlanApplicationService(
                    new CreatePresentationPlanFromSprintReviewUseCase(
                            new SprintReviewToPresentationPlanMapper(),
                            mock(GeneratePresentationPlanUseCase.class),
                            new OpenAiProperties(false, false, "", "", "", "", "gpt-test", "", Duration.ofSeconds(30), 1200, 0.2),
                            new PresentationAiProperties(false, true)
                    )
            ),
            new PresentationPlanToPresentationDeckMapper(
                    new DeckLayoutEngine(new SlideTemplateRegistry(), new PresentationThemeApplicationService(new ThemeRegistry("corporate-clean")))
            )
    );

    @Test
    void shouldCreateStructuredEditableDeckFromSprintReview() {
        var artifact = TestSprintReviewFactory.artifact();
        var review = TestSprintReviewFactory.reviewWithHighlight();

        var deck = mapper.toDeck(artifact, review);

        assertThat(deck.referenceId()).isEqualTo(String.valueOf(review.externalSprintId()));
        assertThat(deck.slides()).hasSize(9);
        assertThat(deck.slides().get(0).slideType()).isEqualTo(SlideType.TITLE);
        assertThat(deck.slides().stream().filter(slide -> slide.templateType() == SlideTemplateType.SECTION_DIVIDER)).hasSize(3);
        assertThat(deck.slides().get(3).title()).isEqualTo("Key themes");
        assertThat(deck.slides().get(0).elements()).isNotEmpty();
    }
}

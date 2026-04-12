package com.willmear.sprint.presentation.template;

import static org.assertj.core.api.Assertions.assertThat;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.theme.application.PresentationThemeApplicationService;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import com.willmear.sprint.presentationplan.application.CreatePresentationPlanFromSprintReviewUseCase;
import com.willmear.sprint.presentationplan.application.GeneratePresentationPlanUseCase;
import com.willmear.sprint.presentationplan.mapper.SprintReviewToPresentationPlanMapper;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

class DeckLayoutEngineTest {

    private final CreatePresentationPlanFromSprintReviewUseCase createPresentationPlanFromSprintReviewUseCase =
            new CreatePresentationPlanFromSprintReviewUseCase(
                    new SprintReviewToPresentationPlanMapper(),
                    mock(GeneratePresentationPlanUseCase.class),
                    new OpenAiProperties(false, false, "", "", "", "", "gpt-test", "", Duration.ofSeconds(30), 1200, 0.2),
                    new PresentationAiProperties(false, true)
            );
    private final DeckLayoutEngine deckLayoutEngine = new DeckLayoutEngine(
            new SlideTemplateRegistry(),
            new PresentationThemeApplicationService(new ThemeRegistry("corporate-clean"))
    );

    @Test
    void shouldRenderThemeSlidesUsingStructuredTemplateSlots() {
        UUID workspaceId = UUID.randomUUID();
        var plan = createPresentationPlanFromSprintReviewUseCase.create(
                workspaceId,
                "SPRINT",
                "42",
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")
        );

        var deck = deckLayoutEngine.layout(TestSprintReviewFactory.artifact(), plan);

        assertThat(deck.slides()).hasSize(9);
        assertThat(deck.slides().get(2).templateType()).isEqualTo(SlideTemplateType.SECTION_DIVIDER);
        assertThat(deck.slides().get(3).slideType()).isEqualTo(SlideType.THEMES);
        assertThat(deck.slides().get(3).templateType()).isEqualTo(SlideTemplateType.TWO_COLUMN_HIGHLIGHTS);
        assertThat(deck.slides().get(3).layoutType()).isEqualTo(SlideLayoutType.TITLE_BODY_NOTES);
        assertThat(deck.slides().get(3).elements())
                .extracting(element -> element.role())
                .contains(SlideElementRole.SECTION_LABEL, SlideElementRole.TITLE, SlideElementRole.BODY_BULLETS);
    }

    @Test
    void shouldSplitOverflowingHighlightBulletsIntoAdditionalDeckSlides() {
        UUID workspaceId = UUID.randomUUID();
        SprintReview review = new SprintReview(
                UUID.randomUUID(),
                workspaceId,
                42L,
                "Sprint 42",
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT").summary(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT").themes(),
                List.of(
                        new SprintHighlight("Highlight 1", "Delivered 1", List.of("SPR-1"), "FEATURE"),
                        new SprintHighlight("Highlight 2", "Delivered 2", List.of("SPR-2"), "FEATURE"),
                        new SprintHighlight("Highlight 3", "Delivered 3", List.of("SPR-3"), "FEATURE"),
                        new SprintHighlight("Highlight 4", "Delivered 4", List.of("SPR-4"), "FEATURE"),
                        new SprintHighlight("Highlight 5", "Delivered 5", List.of("SPR-5"), "FEATURE")
                ),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT").blockers(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT").speakerNotes(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT").generatedAt(),
                "DIRECT",
                "GENERATED"
        );

        var plan = createPresentationPlanFromSprintReviewUseCase.create(workspaceId, "SPRINT", "42", review);
        var deck = deckLayoutEngine.layout(TestSprintReviewFactory.artifact(), plan);

        assertThat(deck.slides().stream().filter(slide -> slide.slideType() == SlideType.HIGHLIGHTS)).hasSize(2);
        assertThat(deck.slides().stream().filter(slide -> slide.templateType() == SlideTemplateType.SECTION_DIVIDER)).hasSize(3);
        assertThat(deck.slides().stream()
                .filter(slide -> slide.slideType() == SlideType.HIGHLIGHTS)
                .allMatch(slide -> slide.bulletPoints().size() <= 4)).isTrue();
    }
}

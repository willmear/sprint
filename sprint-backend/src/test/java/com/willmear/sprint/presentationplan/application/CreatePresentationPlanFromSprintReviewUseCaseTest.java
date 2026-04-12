package com.willmear.sprint.presentationplan.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.presentationplan.domain.SlideIntent;
import com.willmear.sprint.presentationplan.domain.SlideBlockType;
import com.willmear.sprint.presentationplan.mapper.SprintReviewToPresentationPlanMapper;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreatePresentationPlanFromSprintReviewUseCaseTest {

    private final CreatePresentationPlanFromSprintReviewUseCase useCase =
            new CreatePresentationPlanFromSprintReviewUseCase(
                    new SprintReviewToPresentationPlanMapper(),
                    mock(GeneratePresentationPlanUseCase.class),
                    new OpenAiProperties(false, false, "", "", "", "", "gpt-test", "", Duration.ofSeconds(30), 1200, 0.2),
                    new PresentationAiProperties(false, true)
            );

    @Test
    void shouldSplitDenseThemesAndHighlightsIntoMultiplePlannedSlides() {
        UUID workspaceId = UUID.randomUUID();
        SprintReview review = new SprintReview(
                UUID.randomUUID(),
                workspaceId,
                42L,
                "Sprint 42",
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT").summary(),
                List.of(
                        new SprintTheme("Theme 1", "Description 1", List.of("SPR-1")),
                        new SprintTheme("Theme 2", "Description 2", List.of("SPR-2")),
                        new SprintTheme("Theme 3", "Description 3", List.of("SPR-3")),
                        new SprintTheme("Theme 4", "Description 4", List.of("SPR-4"))
                ),
                List.of(
                        new SprintHighlight("Highlight 1", "Delivered 1", List.of("SPR-1"), "FEATURE"),
                        new SprintHighlight("Highlight 2", "Delivered 2", List.of("SPR-2"), "FEATURE"),
                        new SprintHighlight("Highlight 3", "Delivered 3", List.of("SPR-3"), "FEATURE"),
                        new SprintHighlight("Highlight 4", "Delivered 4", List.of("SPR-4"), "FEATURE"),
                        new SprintHighlight("Highlight 5", "Delivered 5", List.of("SPR-5"), "FEATURE")
                ),
                List.of(new SprintBlocker("Blocker", "Dependency", List.of("SPR-9"), "MEDIUM")),
                List.of(TestSprintReviewFactory.note()),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT").generatedAt(),
                "DIRECT",
                "GENERATED"
        );

        var plan = useCase.create(workspaceId, "SPRINT", "42", review);

        assertThat(plan.slides()).extracting(slide -> slide.slideIntent()).contains(
                SlideIntent.TITLE,
                SlideIntent.OVERVIEW,
                SlideIntent.THEMES,
                SlideIntent.HIGHLIGHTS,
                SlideIntent.BLOCKERS,
                SlideIntent.CLOSING
        );
        assertThat(plan.slides().stream().filter(slide -> slide.slideIntent() == SlideIntent.THEMES)).hasSize(1);
        assertThat(plan.slides().stream().filter(slide -> slide.slideIntent() == SlideIntent.HIGHLIGHTS)).hasSize(2);
        assertThat(plan.slides().get(2).blocks().get(1).items()).hasSizeLessThanOrEqualTo(3);
        assertThat(plan.slides().stream()
                .filter(slide -> slide.slideIntent() == SlideIntent.HIGHLIGHTS)
                .flatMap(slide -> slide.blocks().stream())
                .filter(block -> !block.items().isEmpty())
                .allMatch(block -> block.items().size() <= 4)).isTrue();
        assertThat(plan.slides().stream()
                .filter(slide -> slide.slideIntent() == SlideIntent.OVERVIEW)
                .flatMap(slide -> slide.blocks().stream())
                .map(block -> block.blockType())
                .toList())
                .contains(SlideBlockType.CALLOUT, SlideBlockType.METRIC, SlideBlockType.BULLETS);
        assertThat(plan.slides().getLast().blocks())
                .extracting(block -> block.blockType())
                .contains(SlideBlockType.CLOSING_NOTE);
        assertThat(plan.slides().stream()
                .filter(slide -> slide.slideIntent() == SlideIntent.THEMES)
                .findFirst()
                .orElseThrow()
                .title())
                .isEqualTo("Key themes");
    }

    @Test
    void shouldFallbackToDeterministicPlanWhenAiPlanningFails() {
        UUID workspaceId = UUID.randomUUID();
        SprintReview review = TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT");
        GeneratePresentationPlanUseCase aiUseCase = mock(GeneratePresentationPlanUseCase.class);
        when(aiUseCase.generate(workspaceId, "SPRINT", "42", review)).thenThrow(new IllegalStateException("boom"));

        CreatePresentationPlanFromSprintReviewUseCase aiBackedUseCase = new CreatePresentationPlanFromSprintReviewUseCase(
                new SprintReviewToPresentationPlanMapper(),
                aiUseCase,
                new OpenAiProperties(true, false, "", "", "", "", "gpt-test", "", Duration.ofSeconds(30), 1200, 0.2),
                new PresentationAiProperties(true, true)
        );

        var plan = aiBackedUseCase.create(workspaceId, "SPRINT", "42", review);

        assertThat(plan.slides()).isNotEmpty();
        assertThat(plan.slides().getFirst().slideIntent()).isEqualTo(SlideIntent.TITLE);
    }
}

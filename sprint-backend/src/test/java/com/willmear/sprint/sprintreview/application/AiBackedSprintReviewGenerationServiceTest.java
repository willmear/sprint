package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.ai.application.SprintReviewAiFacade;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.SprintReviewAiProperties;
import com.willmear.sprint.support.test.ScopedLogLevel;
import com.willmear.sprint.sprintreview.application.support.PlaceholderSprintReviewGenerationService;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiBackedSprintReviewGenerationServiceTest {

    private final SprintReviewAiFacade aiFacade = mock(SprintReviewAiFacade.class);
    private final PlaceholderSprintReviewGenerationService placeholder = mock(PlaceholderSprintReviewGenerationService.class);

    @Test
    void shouldUsePlaceholderWhenOpenAiDisabled() {
        SprintContext context = context();
        SprintReviewGenerationInput input = input();
        AiBackedSprintReviewGenerationService service = new AiBackedSprintReviewGenerationService(
                aiFacade,
                placeholder,
                new OpenAiProperties(false, true, "", "", "", "", "model", "embed", Duration.ofSeconds(5), 500, 0.2),
                new SprintReviewAiProperties(true, true, 100, 5, 2000, 800)
        );
        SprintReview fallback = review("PLACEHOLDER");
        when(placeholder.generate(context, input)).thenReturn(fallback);

        SprintReview generated = service.generate(context, input);

        assertThat(generated).isEqualTo(fallback);
        verify(placeholder).generate(context, input);
    }

    @Test
    void shouldAssembleReviewFromAiSections() {
        SprintContext context = context();
        SprintReviewGenerationInput input = input();
        AiBackedSprintReviewGenerationService service = new AiBackedSprintReviewGenerationService(
                aiFacade,
                placeholder,
                new OpenAiProperties(true, true, "", "", "", "", "gpt-test", "embed", Duration.ofSeconds(5), 500, 0.2),
                new SprintReviewAiProperties(true, true, 100, 5, 2000, 800)
        );
        when(aiFacade.generate(context, "gpt-test", 0.2, 500, "leadership", "concise"))
                .thenReturn(new SprintReviewAiFacade.SprintReviewAiResult(
                        new SprintSummary("AI title", "AI overview", null, null, null),
                        List.of(new SprintTheme("Theme", "Description", List.of("SPR-1"))),
                        List.of(new SprintHighlight("Highlight", "Delivered", List.of("SPR-1"), "FEATURE")),
                        List.of(new SprintBlocker("Blocker", "Dependency", List.of("SPR-2"), "MEDIUM")),
                        List.of(new SpeakerNote("Intro", "AI note", 1))
                ));

        SprintReview generated = service.generate(context, input);

        assertThat(generated.summary().title()).isEqualTo("AI title");
        assertThat(generated.highlights()).hasSize(1);
        assertThat(generated.blockers()).hasSize(1);
        assertThat(generated.generationSource()).isEqualTo("DIRECT");
    }

    @Test
    void shouldFallbackToPlaceholderWhenAiFails() {
        SprintContext context = context();
        SprintReviewGenerationInput input = input();
        AiBackedSprintReviewGenerationService service = new AiBackedSprintReviewGenerationService(
                aiFacade,
                placeholder,
                new OpenAiProperties(true, true, "", "", "", "", "gpt-test", "embed", Duration.ofSeconds(5), 500, 0.2),
                new SprintReviewAiProperties(true, true, 100, 5, 2000, 800)
        );
        SprintReview fallback = review("PLACEHOLDER");
        doThrow(new IllegalStateException("boom")).when(aiFacade).generate(context, "gpt-test", 0.2, 500, "leadership", "concise");
        when(placeholder.generate(context, input)).thenReturn(fallback);

        SprintReview generated;
        try (ScopedLogLevel ignored = ScopedLogLevel.off(AiBackedSprintReviewGenerationService.class)) {
            generated = service.generate(context, input);
        }

        assertThat(generated).isEqualTo(fallback);
    }

    private SprintContext context() {
        return new SprintContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                42L,
                "Sprint 42",
                "Goal",
                "ACTIVE",
                Instant.now(),
                Instant.now(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                0,
                0,
                0,
                Instant.now()
        );
    }

    private SprintReviewGenerationInput input() {
        return new SprintReviewGenerationInput(UUID.randomUUID(), 42L, true, true, "leadership", "concise", "DIRECT");
    }

    private SprintReview review(String source) {
        return new SprintReview(UUID.randomUUID(), UUID.randomUUID(), 42L, "Sprint 42",
                new SprintSummary("Fallback", "Overview", null, null, null), List.of(), List.of(), List.of(), List.of(), Instant.now(), source, "GENERATED");
    }
}

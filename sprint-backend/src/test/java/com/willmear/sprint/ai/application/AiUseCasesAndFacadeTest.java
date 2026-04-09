package com.willmear.sprint.ai.application;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.AiRun;
import com.willmear.sprint.ai.domain.model.TokenUsage;
import com.willmear.sprint.ai.domain.service.AiResponseValidator;
import com.willmear.sprint.ai.model.SpeakerNoteAiResponse;
import com.willmear.sprint.ai.model.SprintBlockerAiResponse;
import com.willmear.sprint.ai.model.SprintHighlightAiResponse;
import com.willmear.sprint.ai.model.SprintReviewAiResponse;
import com.willmear.sprint.ai.model.SprintSummaryAiResponse;
import com.willmear.sprint.ai.model.SprintThemeAiResponse;
import com.willmear.sprint.ai.parser.SprintReviewParser;
import com.willmear.sprint.ai.prompt.builder.SprintReviewPromptBuilder;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiUseCasesAndFacadeTest {

    @Test
    void shouldCreateSingleStructuredGenerationRequestInFacade() {
        AiGenerationService aiGenerationService = mock(AiGenerationService.class);
        SprintReviewPromptBuilder promptBuilder = mock(SprintReviewPromptBuilder.class);
        SprintReviewParser sprintReviewParser = mock(SprintReviewParser.class);
        AiResponseValidator aiResponseValidator = mock(AiResponseValidator.class);
        SprintReviewAiFacade facade = new SprintReviewAiFacade(aiGenerationService, promptBuilder, sprintReviewParser, aiResponseValidator);
        var context = TestSprintReviewFactory.contextWithIssues();
        AiPrompt prompt = new AiPrompt("sprint-review", "v2", "system", "user", "json-object", Map.of());
        SprintReviewAiResponse payload = payload();

        when(promptBuilder.build(context, "leadership", "concise", "gpt-test")).thenReturn(prompt);
        when(aiGenerationService.generate(any(AiGenerationRequest.class), any()))
                .thenReturn(new AiGenerationResult<>(payload, aiResponse(), aiRun("sprint-review")));
        when(aiResponseValidator.validateSprintReviewPayload(payload)).thenReturn(payload);

        var result = facade.generate(context, "gpt-test", 0.2, 900, "leadership", "concise");

        ArgumentCaptor<AiGenerationRequest> requestCaptor = ArgumentCaptor.forClass(AiGenerationRequest.class);
        verify(aiGenerationService).generate(requestCaptor.capture(), any());
        AiGenerationRequest request = requestCaptor.getValue();
        assertThat(request.workflowName()).isEqualTo("sprint-review");
        assertThat(request.promptName()).isEqualTo("sprint-review");
        assertThat(request.structuredOutputExpected()).isTrue();
        assertThat(result.summary().title()).isEqualTo("Sprint Review");
        assertThat(result.themes()).hasSize(1);
        assertThat(result.highlights()).hasSize(1);
        assertThat(result.blockers()).hasSize(1);
        assertThat(result.speakerNotes()).hasSize(1);
    }

    private static SprintReviewAiResponse payload() {
        return new SprintReviewAiResponse(
                new SprintSummaryAiResponse("Sprint Review", "Overview", "Delivery", "Quality", "Outcome"),
                List.of(new SprintThemeAiResponse("Theme", "Description", List.of("SPR-1"))),
                List.of(new SprintHighlightAiResponse("Highlight", "Delivered", List.of("SPR-1"), "FEATURE")),
                List.of(new SprintBlockerAiResponse("Blocker", "Dependency", List.of("SPR-2"), "MEDIUM")),
                List.of(new SpeakerNoteAiResponse("Intro", "Start here", 1))
        );
    }

    private static AiResponse aiResponse() {
        return new AiResponse("{}", "gpt-test", new TokenUsage(1, 1, 2), Instant.now(), "stop", true, null);
    }

    private static AiRun aiRun(String workflow) {
        return new AiRun(java.util.UUID.randomUUID(), workflow, "prompt", "v1", "gpt-test", Instant.now(), Instant.now(), new TokenUsage(1, 1, 2), true, null);
    }
}

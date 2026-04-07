package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.model.SpeakerNotesGenerationRequest;
import com.willmear.sprint.ai.model.SprintSummaryGenerationRequest;
import com.willmear.sprint.ai.model.ThemeGenerationRequest;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SprintReviewAiFacade {

    private final GenerateSprintSummaryUseCase generateSprintSummaryUseCase;
    private final GenerateThemesUseCase generateThemesUseCase;
    private final GenerateSpeakerNotesUseCase generateSpeakerNotesUseCase;

    public SprintReviewAiFacade(
            GenerateSprintSummaryUseCase generateSprintSummaryUseCase,
            GenerateThemesUseCase generateThemesUseCase,
            GenerateSpeakerNotesUseCase generateSpeakerNotesUseCase
    ) {
        this.generateSprintSummaryUseCase = generateSprintSummaryUseCase;
        this.generateThemesUseCase = generateThemesUseCase;
        this.generateSpeakerNotesUseCase = generateSpeakerNotesUseCase;
    }

    public SprintReviewAiResult generate(SprintContext context, String model, Double temperature, Integer maxOutputTokens, String audience, String tone) {
        AiGenerationResult<SprintSummary> summary = generateSprintSummaryUseCase.generate(
                new SprintSummaryGenerationRequest(context, model, temperature, maxOutputTokens, audience, tone)
        );
        AiGenerationResult<List<SprintTheme>> themes = generateThemesUseCase.generate(
                new ThemeGenerationRequest(context, model, temperature, maxOutputTokens, audience, tone)
        );
        AiGenerationResult<List<SpeakerNote>> speakerNotes = generateSpeakerNotesUseCase.generate(
                new SpeakerNotesGenerationRequest(context, model, temperature, maxOutputTokens, audience, tone)
        );
        return new SprintReviewAiResult(summary.parsedResult(), themes.parsedResult(), speakerNotes.parsedResult());
    }

    public record SprintReviewAiResult(
            SprintSummary summary,
            List<SprintTheme> themes,
            List<SpeakerNote> speakerNotes
    ) {
    }
}

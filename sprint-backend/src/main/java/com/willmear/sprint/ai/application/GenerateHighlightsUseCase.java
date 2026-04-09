package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.model.HighlightGenerationRequest;
import com.willmear.sprint.ai.parser.SprintHighlightsParser;
import com.willmear.sprint.ai.prompt.builder.SprintHighlightsPromptBuilder;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GenerateHighlightsUseCase {

    private final AiGenerationService aiGenerationService;
    private final SprintHighlightsPromptBuilder sprintHighlightsPromptBuilder;
    private final SprintHighlightsParser sprintHighlightsParser;

    public GenerateHighlightsUseCase(
            AiGenerationService aiGenerationService,
            SprintHighlightsPromptBuilder sprintHighlightsPromptBuilder,
            SprintHighlightsParser sprintHighlightsParser
    ) {
        this.aiGenerationService = aiGenerationService;
        this.sprintHighlightsPromptBuilder = sprintHighlightsPromptBuilder;
        this.sprintHighlightsParser = sprintHighlightsParser;
    }

    public AiGenerationResult<List<SprintHighlight>> generate(HighlightGenerationRequest request) {
        AiGenerationRequest aiRequest = new AiGenerationRequest(
                "sprint-review-highlights",
                "sprint-highlights",
                request.model(),
                sprintHighlightsPromptBuilder.build(request.context(), request.audience(), request.tone(), request.model()),
                request.temperature(),
                request.maxOutputTokens(),
                false,
                "sprint-highlights"
        );
        return aiGenerationService.generate(aiRequest, sprintHighlightsParser::parse);
    }
}

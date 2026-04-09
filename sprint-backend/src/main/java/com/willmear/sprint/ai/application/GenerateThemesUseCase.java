package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.model.ThemeGenerationRequest;
import com.willmear.sprint.ai.parser.ThemeParser;
import com.willmear.sprint.ai.prompt.builder.ThemeExtractionPromptBuilder;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GenerateThemesUseCase {

    private final AiGenerationService aiGenerationService;
    private final ThemeExtractionPromptBuilder themeExtractionPromptBuilder;
    private final ThemeParser themeParser;

    public GenerateThemesUseCase(
            AiGenerationService aiGenerationService,
            ThemeExtractionPromptBuilder themeExtractionPromptBuilder,
            ThemeParser themeParser
    ) {
        this.aiGenerationService = aiGenerationService;
        this.themeExtractionPromptBuilder = themeExtractionPromptBuilder;
        this.themeParser = themeParser;
    }

    public AiGenerationResult<List<SprintTheme>> generate(ThemeGenerationRequest request) {
        AiGenerationRequest aiRequest = new AiGenerationRequest(
                "sprint-review-themes",
                "sprint-themes",
                request.model(),
                themeExtractionPromptBuilder.build(request.context(), request.audience(), request.tone(), request.model()),
                request.temperature(),
                request.maxOutputTokens(),
                false,
                "sprint-themes"
        );
        return aiGenerationService.generate(aiRequest, themeParser::parse);
    }
}

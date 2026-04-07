package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.model.SprintSummaryGenerationRequest;
import com.willmear.sprint.ai.parser.SprintSummaryParser;
import com.willmear.sprint.ai.prompt.builder.SprintSummaryPromptBuilder;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import org.springframework.stereotype.Service;

@Service
public class GenerateSprintSummaryUseCase {

    private final AiGenerationService aiGenerationService;
    private final SprintSummaryPromptBuilder sprintSummaryPromptBuilder;
    private final SprintSummaryParser sprintSummaryParser;

    public GenerateSprintSummaryUseCase(
            AiGenerationService aiGenerationService,
            SprintSummaryPromptBuilder sprintSummaryPromptBuilder,
            SprintSummaryParser sprintSummaryParser
    ) {
        this.aiGenerationService = aiGenerationService;
        this.sprintSummaryPromptBuilder = sprintSummaryPromptBuilder;
        this.sprintSummaryParser = sprintSummaryParser;
    }

    public AiGenerationResult<SprintSummary> generate(SprintSummaryGenerationRequest request) {
        AiGenerationRequest aiRequest = new AiGenerationRequest(
                "sprint-review-summary",
                "sprint-summary",
                request.model(),
                sprintSummaryPromptBuilder.build(request.context(), request.audience(), request.tone(), request.model()),
                request.temperature(),
                request.maxOutputTokens(),
                true,
                "sprint-summary"
        );
        return aiGenerationService.generate(aiRequest, sprintSummaryParser::parse);
    }
}

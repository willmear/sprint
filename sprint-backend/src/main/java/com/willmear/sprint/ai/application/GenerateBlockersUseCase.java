package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.model.BlockerGenerationRequest;
import com.willmear.sprint.ai.parser.SprintBlockersParser;
import com.willmear.sprint.ai.prompt.builder.SprintBlockersPromptBuilder;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GenerateBlockersUseCase {

    private final AiGenerationService aiGenerationService;
    private final SprintBlockersPromptBuilder sprintBlockersPromptBuilder;
    private final SprintBlockersParser sprintBlockersParser;

    public GenerateBlockersUseCase(
            AiGenerationService aiGenerationService,
            SprintBlockersPromptBuilder sprintBlockersPromptBuilder,
            SprintBlockersParser sprintBlockersParser
    ) {
        this.aiGenerationService = aiGenerationService;
        this.sprintBlockersPromptBuilder = sprintBlockersPromptBuilder;
        this.sprintBlockersParser = sprintBlockersParser;
    }

    public AiGenerationResult<List<SprintBlocker>> generate(BlockerGenerationRequest request) {
        AiGenerationRequest aiRequest = new AiGenerationRequest(
                "sprint-review-blockers",
                "sprint-blockers",
                request.model(),
                sprintBlockersPromptBuilder.build(request.context(), request.audience(), request.tone(), request.model()),
                request.temperature(),
                request.maxOutputTokens(),
                false,
                "sprint-blockers"
        );
        return aiGenerationService.generate(aiRequest, sprintBlockersParser::parse);
    }
}

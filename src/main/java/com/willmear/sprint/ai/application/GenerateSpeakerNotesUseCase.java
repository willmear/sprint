package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.prompt.builder.SpeakerNotesPromptBuilder;
import com.willmear.sprint.ai.parser.SpeakerNotesParser;
import com.willmear.sprint.ai.model.SpeakerNotesGenerationRequest;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GenerateSpeakerNotesUseCase {

    private final AiGenerationService aiGenerationService;
    private final SpeakerNotesPromptBuilder speakerNotesPromptBuilder;
    private final SpeakerNotesParser speakerNotesParser;

    public GenerateSpeakerNotesUseCase(
            AiGenerationService aiGenerationService,
            SpeakerNotesPromptBuilder speakerNotesPromptBuilder,
            SpeakerNotesParser speakerNotesParser
    ) {
        this.aiGenerationService = aiGenerationService;
        this.speakerNotesPromptBuilder = speakerNotesPromptBuilder;
        this.speakerNotesParser = speakerNotesParser;
    }

    public AiGenerationResult<List<SpeakerNote>> generate(SpeakerNotesGenerationRequest request) {
        AiGenerationRequest aiRequest = new AiGenerationRequest(
                "sprint-review-speaker-notes",
                "sprint-speaker-notes",
                request.model(),
                speakerNotesPromptBuilder.build(request.context(), request.audience(), request.tone(), request.model()),
                request.temperature(),
                request.maxOutputTokens(),
                true,
                "speaker-notes"
        );
        return aiGenerationService.generate(aiRequest, speakerNotesParser::parse);
    }
}

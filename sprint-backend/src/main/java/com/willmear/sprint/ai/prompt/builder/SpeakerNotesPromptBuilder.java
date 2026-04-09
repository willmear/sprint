package com.willmear.sprint.ai.prompt.builder;

import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.service.PromptTemplateService;
import com.willmear.sprint.ai.prompt.registry.PromptVersionRegistry;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SpeakerNotesPromptBuilder {

    private final PromptTemplateService promptTemplateService;

    public SpeakerNotesPromptBuilder(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    public AiPrompt build(SprintContext context, String audience, String tone, String model) {
        if (context == null) {
            throw new AiPromptBuildException("Sprint context is required for speaker notes prompt.");
        }
        return new AiPrompt(
                "sprint-speaker-notes",
                PromptVersionRegistry.SPEAKER_NOTES_VERSION,
                promptTemplateService.systemTemplate("sprint-speaker-notes"),
                promptTemplateService.userTemplatePrefix("sprint-speaker-notes")
                        + "\nSprint: " + context.sprintName()
                        + "\nGoal: " + context.sprintGoal()
                        + "\nCompleted issues: " + context.completedIssues().size()
                        + "\nCarried over issues: " + context.carriedOverIssues().size()
                        + "\nAudience: " + audience
                        + "\nTone: " + tone
                        + "\nReturn JSON array with fields section, note, displayOrder.",
                "json-array",
                Map.of("workflow", "speaker-notes", "model", model == null ? "" : model)
        );
    }
}

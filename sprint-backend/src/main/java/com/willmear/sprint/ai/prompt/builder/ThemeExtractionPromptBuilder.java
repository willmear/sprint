package com.willmear.sprint.ai.prompt.builder;

import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.service.PromptTemplateService;
import com.willmear.sprint.ai.prompt.registry.PromptVersionRegistry;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ThemeExtractionPromptBuilder {

    private final PromptTemplateService promptTemplateService;

    public ThemeExtractionPromptBuilder(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    public AiPrompt build(SprintContext context, String audience, String tone, String model) {
        if (context == null) {
            throw new AiPromptBuildException("Sprint context is required for theme extraction prompt.");
        }
        return new AiPrompt(
                "sprint-themes",
                PromptVersionRegistry.THEME_EXTRACTION_VERSION,
                promptTemplateService.systemTemplate("sprint-themes"),
                promptTemplateService.userTemplatePrefix("sprint-themes")
                        + "\nSprint: " + context.sprintName()
                        + "\nCompleted issue keys: " + context.completedIssues().stream().map(issue -> issue.issueKey()).toList()
                        + "\nBug fix issue keys: " + context.bugFixes().stream().map(issue -> issue.issueKey()).toList()
                        + "\nTechnical issue keys: " + context.technicalImprovements().stream().map(issue -> issue.issueKey()).toList()
                        + "\nAudience: " + audience
                        + "\nTone: " + tone
                        + "\nReturn JSON array of themes with name, description, relatedIssueKeys.",
                "json-array",
                Map.of("workflow", "themes", "model", model == null ? "" : model)
        );
    }
}

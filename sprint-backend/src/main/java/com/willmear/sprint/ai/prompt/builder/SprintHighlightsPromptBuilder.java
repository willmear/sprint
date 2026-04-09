package com.willmear.sprint.ai.prompt.builder;

import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.service.PromptTemplateService;
import com.willmear.sprint.ai.prompt.registry.PromptVersionRegistry;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SprintHighlightsPromptBuilder {

    private final PromptTemplateService promptTemplateService;

    public SprintHighlightsPromptBuilder(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    public AiPrompt build(SprintContext context, String audience, String tone, String model) {
        if (context == null) {
            throw new AiPromptBuildException("Sprint context is required for sprint highlights prompt.");
        }
        return new AiPrompt(
                "sprint-highlights",
                PromptVersionRegistry.SPRINT_HIGHLIGHTS_VERSION,
                promptTemplateService.systemTemplate("sprint-highlights"),
                promptTemplateService.userTemplatePrefix("sprint-highlights")
                        + "\nSprint: " + context.sprintName()
                        + "\nGoal: " + context.sprintGoal()
                        + "\nCompleted issues: " + context.completedIssues().stream()
                                .map(issue -> issue.issueKey() + " | " + issue.summary() + " | " + defaultText(issue.description()))
                                .toList()
                        + "\nNotable comments: " + context.notableComments()
                        + "\nAudience: " + audience
                        + "\nTone: " + tone
                        + "\nReturn JSON array with title, description, relatedIssueKeys, category."
                        + "\nUse category values FEATURE, BUGFIX, IMPROVEMENT, or DELIVERY.",
                "json-array",
                Map.of("workflow", "highlights", "model", model == null ? "" : model)
        );
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "No description provided." : value;
    }
}

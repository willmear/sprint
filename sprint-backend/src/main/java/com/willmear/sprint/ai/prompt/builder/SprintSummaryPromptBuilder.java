package com.willmear.sprint.ai.prompt.builder;

import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.service.PromptTemplateService;
import com.willmear.sprint.ai.prompt.registry.PromptVersionRegistry;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SprintSummaryPromptBuilder {

    private final PromptTemplateService promptTemplateService;

    public SprintSummaryPromptBuilder(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    public AiPrompt build(SprintContext context, String audience, String tone, String model) {
        if (context == null) {
            throw new AiPromptBuildException("Sprint context is required for sprint summary prompt.");
        }
        return new AiPrompt(
                "sprint-summary",
                PromptVersionRegistry.SPRINT_SUMMARY_VERSION,
                promptTemplateService.systemTemplate("sprint-summary"),
                promptTemplateService.userTemplatePrefix("sprint-summary")
                        + "\nSprint: " + context.sprintName()
                        + "\nGoal: " + context.sprintGoal()
                        + "\nCompleted issues: " + context.completedIssues().size()
                        + "\nBug fixes: " + context.bugFixes().size()
                        + "\nTechnical improvements: " + context.technicalImprovements().size()
                        + "\nAudience: " + audience
                        + "\nTone: " + tone
                        + "\nReturn JSON object with title, overview, deliverySummary, qualitySummary, outcomeSummary.",
                "json-object",
                Map.of("workflow", "summary", "model", model == null ? "" : model)
        );
    }
}

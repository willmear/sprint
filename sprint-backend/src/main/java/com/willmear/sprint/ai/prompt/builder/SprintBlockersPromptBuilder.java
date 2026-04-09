package com.willmear.sprint.ai.prompt.builder;

import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.service.PromptTemplateService;
import com.willmear.sprint.ai.prompt.registry.PromptVersionRegistry;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SprintBlockersPromptBuilder {

    private final PromptTemplateService promptTemplateService;

    public SprintBlockersPromptBuilder(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    public AiPrompt build(SprintContext context, String audience, String tone, String model) {
        if (context == null) {
            throw new AiPromptBuildException("Sprint context is required for sprint blockers prompt.");
        }
        return new AiPrompt(
                "sprint-blockers",
                PromptVersionRegistry.SPRINT_BLOCKERS_VERSION,
                promptTemplateService.systemTemplate("sprint-blockers"),
                promptTemplateService.userTemplatePrefix("sprint-blockers")
                        + "\nSprint: " + context.sprintName()
                        + "\nGoal: " + context.sprintGoal()
                        + "\nCarried over issues: " + context.carriedOverIssues().stream()
                                .map(issue -> issue.issueKey() + " | " + issue.summary() + " | status=" + issue.status())
                                .toList()
                        + "\nKnown blockers: " + context.blockers()
                        + "\nNotable comments: " + context.notableComments()
                        + "\nAudience: " + audience
                        + "\nTone: " + tone
                        + "\nReturn JSON array with title, description, relatedIssueKeys, severity."
                        + "\nUse severity values LOW, MEDIUM, HIGH, or CRITICAL."
                        + "\nIf there are no blockers, return an empty JSON array.",
                "json-array",
                Map.of("workflow", "blockers", "model", model == null ? "" : model)
        );
    }
}

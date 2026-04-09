package com.willmear.sprint.ai.prompt.builder;

import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.model.SprintReviewPromptInput;
import com.willmear.sprint.ai.prompt.registry.PromptVersionRegistry;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewPromptBuilder {

    private final SprintPromptCompressionService sprintPromptCompressionService;
    private final SprintReviewPromptFormatter sprintReviewPromptFormatter;

    public SprintReviewPromptBuilder(
            SprintPromptCompressionService sprintPromptCompressionService,
            SprintReviewPromptFormatter sprintReviewPromptFormatter
    ) {
        this.sprintPromptCompressionService = sprintPromptCompressionService;
        this.sprintReviewPromptFormatter = sprintReviewPromptFormatter;
    }

    public AiPrompt build(SprintContext context, String audience, String tone, String model) {
        if (context == null) {
            throw new AiPromptBuildException("Sprint context is required for sprint review prompt.");
        }

        SprintReviewPromptInput promptInput = sprintPromptCompressionService.buildPromptInput(context);
        String payloadJson = sprintReviewPromptFormatter.format(promptInput);
        return new AiPrompt(
                "sprint-review",
                PromptVersionRegistry.SPRINT_REVIEW_VERSION,
                buildSystemPrompt(),
                buildUserPrompt(promptInput, payloadJson, audience, tone),
                "json-object",
                metadata(context, model, audience, tone)
        );
    }

    private String buildSystemPrompt() {
        return """
                You generate sprint review summaries for engineering teams.
                Stay grounded in the provided sprint data only.
                Do not invent work, outcomes, blockers, or issue keys.
                Focus on delivered impact, quality work, execution risks, and clear presentation language.
                Output valid JSON only with no markdown fences and no extra commentary.
                Use concise language suitable for a sprint review or show-and-tell presentation.
                """;
    }

    private String buildUserPrompt(SprintReviewPromptInput promptInput, String payloadJson, String audience, String tone) {
        return """
                Generate a structured sprint review from the synced sprint data below.

                Audience: %s
                Tone: %s

                Requirements:
                - Base all conclusions on the sprint metadata, issues, statuses, and issue comments in the payload.
                - Group related delivered work into themes.
                - Identify concrete highlights tied to real issue keys.
                - Identify blockers or risks from unfinished work, blocked statuses, or issue comments.
                - Speaker notes should help a presenter walk through the sprint in 4 to 6 short sections.
                - Prefer impact-focused summaries over repeating ticket text verbatim.
                - If the sprint goal is empty, do not invent one.
                - If there are no blockers, return an empty blockers array.

                Return JSON only using this schema:
                {
                  "summary": {
                    "title": "string",
                    "overview": "string",
                    "deliverySummary": "string",
                    "qualitySummary": "string",
                    "outcomeSummary": "string"
                  },
                  "themes": [
                    {
                      "name": "string",
                      "description": "string",
                      "relatedIssueKeys": ["ABC-123"]
                    }
                  ],
                  "highlights": [
                    {
                      "title": "string",
                      "description": "string",
                      "relatedIssueKeys": ["ABC-123"],
                      "category": "FEATURE|BUGFIX|TECH_DEBT|IMPROVEMENT"
                    }
                  ],
                  "blockers": [
                    {
                      "title": "string",
                      "description": "string",
                      "relatedIssueKeys": ["ABC-123"],
                      "severity": "LOW|MEDIUM|HIGH"
                    }
                  ],
                  "speakerNotes": [
                    {
                      "section": "string",
                      "note": "string",
                      "displayOrder": 1
                    }
                  ]
                }

                Sprint payload:
                %s
                """.formatted(
                safe(audience),
                safe(tone),
                payloadJson
        );
    }

    private Map<String, Object> metadata(SprintContext context, String model, String audience, String tone) {
        LinkedHashMap<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("workflow", "sprint-review");
        metadata.put("model", model);
        metadata.put("workspaceId", context.workspaceId());
        metadata.put("sprintId", context.externalSprintId());
        metadata.put("audience", safe(audience));
        metadata.put("tone", safe(tone));
        return metadata;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "default" : value.trim();
    }
}

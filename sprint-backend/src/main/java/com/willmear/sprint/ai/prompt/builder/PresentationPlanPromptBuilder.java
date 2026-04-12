package com.willmear.sprint.ai.prompt.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.prompt.registry.PromptVersionRegistry;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PresentationPlanPromptBuilder {

    private final ObjectMapper objectMapper;

    public PresentationPlanPromptBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiPrompt build(SprintReview sprintReview, String model) {
        if (sprintReview == null) {
            throw new AiPromptBuildException("Sprint review is required for presentation plan prompt.");
        }

        return new AiPrompt(
                "presentation-plan",
                PromptVersionRegistry.PRESENTATION_PLAN_VERSION,
                buildSystemPrompt(),
                buildUserPrompt(sprintReview),
                "json-object",
                metadata(sprintReview, model)
        );
    }

    private String buildSystemPrompt() {
        return """
                You generate structured presentation plans for sprint reviews.
                Stay grounded in the provided sprint review only.
                Do not invent delivery items, blockers, metrics, or issue keys.
                Produce concise slide-ready content, not long prose.
                Separate visible slide content from speaker notes.
                Avoid overcrowding and avoid repeating the same information across slides.
                Optimize for presentation design, not document summarization.
                Prefer short titles, strong section names, callout-worthy takeaways, and 3-5 bullets per slide.
                Put detail, caveats, and narration into speaker notes instead of on-slide paragraphs.
                Output valid JSON only with no markdown fences and no extra commentary.
                """;
    }

    private String buildUserPrompt(SprintReview sprintReview) {
        return """
                Convert the sprint review below into a presentation-ready slide plan.

                Requirements:
                - Create short slide titles, usually 2-6 words.
                - Keep section names strong and presentation-ready.
                - Keep bullets presentation-friendly, brief, and scannable.
                - Separate slide text from speaker notes.
                - Avoid overcrowding each slide.
                - Prefer 3-5 bullets per slide.
                - Avoid long paragraphs on slides.
                - Put supporting detail into speaker notes instead of slide text.
                - Use callout blocks for the strongest outcome or takeaway on a slide.
                - Use metric blocks when a count or compact summary helps.
                - Ground every slide in actual sprint review content.
                - Prefer a strong presentation flow:
                  1. Title
                  2. Overview
                  3. Themes
                  4. Highlights
                  5. Blockers or risks
                  6. Closing
                - Make the output feel visually structured:
                  - use TITLE_ONLY when a divider-style slide is appropriate
                  - use TWO_COLUMN for grouped themes or grouped highlights
                  - use CALLOUT when one takeaway should lead the slide
                - Use multiple theme/highlight slides only when the content needs to be split.
                - Do not emit empty placeholder slides.

                Return JSON only using this schema:
                {
                  "title": "string",
                  "subtitle": "string",
                  "slides": [
                    {
                      "slideIntent": "TITLE|OVERVIEW|THEMES|HIGHLIGHTS|METRICS|BLOCKERS|CLOSING",
                      "title": "string",
                      "subtitle": "string",
                      "layoutHint": "TITLE_ONLY|TITLE_AND_BULLETS|TWO_COLUMN|CALLOUT|SECTION_SUMMARY",
                      "blocks": [
                        {
                          "blockType": "TITLE|SUBTITLE|BULLETS|CALLOUT|METRIC|NOTES|SECTION_LABEL|CLOSING_NOTE",
                          "text": "string",
                          "items": ["string"],
                          "visualPriority": "PRIMARY|SECONDARY|SUPPORTING"
                        }
                      ],
                      "speakerNotes": "string"
                    }
                  ]
                }

                Sprint review payload:
                %s
                """.formatted(serialize(sprintReview));
    }

    private Map<String, Object> metadata(SprintReview sprintReview, String model) {
        LinkedHashMap<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("workflow", "presentation-plan");
        metadata.put("model", model);
        metadata.put("workspaceId", sprintReview.workspaceId());
        metadata.put("sprintId", sprintReview.externalSprintId());
        return metadata;
    }

    private String serialize(SprintReview sprintReview) {
        try {
            return objectMapper.writeValueAsString(sprintReview);
        } catch (JsonProcessingException exception) {
            throw new AiPromptBuildException("Unable to serialize sprint review for presentation plan prompt.", exception);
        }
    }
}

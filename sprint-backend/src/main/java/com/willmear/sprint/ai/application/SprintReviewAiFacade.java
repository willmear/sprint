package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.model.SpeakerNoteAiResponse;
import com.willmear.sprint.ai.model.SprintBlockerAiResponse;
import com.willmear.sprint.ai.model.SprintHighlightAiResponse;
import com.willmear.sprint.ai.model.SprintReviewAiResponse;
import com.willmear.sprint.ai.model.SprintThemeAiResponse;
import com.willmear.sprint.ai.parser.SprintReviewParser;
import com.willmear.sprint.ai.prompt.builder.SprintReviewPromptBuilder;
import com.willmear.sprint.ai.domain.service.AiResponseValidator;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SprintReviewAiFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SprintReviewAiFacade.class);

    private final AiGenerationService aiGenerationService;
    private final SprintReviewPromptBuilder sprintReviewPromptBuilder;
    private final SprintReviewParser sprintReviewParser;
    private final AiResponseValidator aiResponseValidator;

    public SprintReviewAiFacade(
            AiGenerationService aiGenerationService,
            SprintReviewPromptBuilder sprintReviewPromptBuilder,
            SprintReviewParser sprintReviewParser,
            AiResponseValidator aiResponseValidator
    ) {
        this.aiGenerationService = aiGenerationService;
        this.sprintReviewPromptBuilder = sprintReviewPromptBuilder;
        this.sprintReviewParser = sprintReviewParser;
        this.aiResponseValidator = aiResponseValidator;
    }

    public SprintReviewAiResult generate(SprintContext context, String model, Double temperature, Integer maxOutputTokens, String audience, String tone) {
        AiGenerationRequest request = new AiGenerationRequest(
                "sprint-review",
                "sprint-review",
                model,
                sprintReviewPromptBuilder.build(context, audience, tone, model),
                temperature,
                maxOutputTokens,
                true,
                "sprint-review"
        );
        AiGenerationResult<SprintReviewAiResponse> result = aiGenerationService.generate(request, sprintReviewParser::parse);
        SprintReviewAiResponse parsed = result.parsedResult();
        LOGGER.info(
                "sprintreview.ai.raw parsedSummary={} parsedThemes={} parsedHighlights={} parsedBlockers={} parsedSpeakerNotes={} contentLength={}",
                parsed != null && parsed.summary() != null,
                parsed != null && parsed.themes() != null ? parsed.themes().size() : -1,
                parsed != null && parsed.highlights() != null ? parsed.highlights().size() : -1,
                parsed != null && parsed.blockers() != null ? parsed.blockers().size() : -1,
                parsed != null && parsed.speakerNotes() != null ? parsed.speakerNotes().size() : -1,
                result.rawResponse() != null && result.rawResponse().content() != null ? result.rawResponse().content().length() : 0
        );
        parsed = aiResponseValidator.validateSprintReviewPayload(parsed);
        return new SprintReviewAiResult(
                new SprintSummary(
                        parsed.summary().title(),
                        parsed.summary().overview(),
                        parsed.summary().deliverySummary(),
                        parsed.summary().qualitySummary(),
                        parsed.summary().outcomeSummary()
                ),
                parsed.themes().stream().map(this::toTheme).toList(),
                parsed.highlights().stream().map(this::toHighlight).toList(),
                parsed.blockers().stream().map(this::toBlocker).toList(),
                parsed.speakerNotes().stream().map(this::toSpeakerNote).sorted(java.util.Comparator.comparing(SpeakerNote::displayOrder)).toList()
        );
    }

    private SprintTheme toTheme(SprintThemeAiResponse theme) {
        return new SprintTheme(
                safe(theme.name()),
                safe(theme.description()),
                theme.relatedIssueKeys() == null ? List.of() : theme.relatedIssueKeys()
        );
    }

    private SprintHighlight toHighlight(SprintHighlightAiResponse highlight) {
        return new SprintHighlight(
                safe(highlight.title()),
                safe(highlight.description()),
                highlight.relatedIssueKeys() == null ? List.of() : highlight.relatedIssueKeys(),
                normalizeEnumValue(highlight.category(), "FEATURE")
        );
    }

    private SprintBlocker toBlocker(SprintBlockerAiResponse blocker) {
        return new SprintBlocker(
                safe(blocker.title()),
                safe(blocker.description()),
                blocker.relatedIssueKeys() == null ? List.of() : blocker.relatedIssueKeys(),
                normalizeEnumValue(blocker.severity(), "MEDIUM")
        );
    }

    private SpeakerNote toSpeakerNote(SpeakerNoteAiResponse note) {
        return new SpeakerNote(
                safe(note.section()),
                safe(note.note()),
                note.displayOrder() == null ? 1 : note.displayOrder()
        );
    }

    private String normalizeEnumValue(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value.trim();
    }

    public record SprintReviewAiResult(
            SprintSummary summary,
            List<SprintTheme> themes,
            List<SprintHighlight> highlights,
            List<SprintBlocker> blockers,
            List<SpeakerNote> speakerNotes
    ) {
    }
}

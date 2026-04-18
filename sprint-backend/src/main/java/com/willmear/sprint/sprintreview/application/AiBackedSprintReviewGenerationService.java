package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.ai.application.SprintReviewAiFacade;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.SprintReviewAiProperties;
import com.willmear.sprint.sprintreview.application.support.PlaceholderSprintReviewGenerationService;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput;
import com.willmear.sprint.sprintreview.domain.port.SprintReviewGenerationPort;
import com.willmear.sprint.sprintreview.domain.service.SprintReviewValidator;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class AiBackedSprintReviewGenerationService implements SprintReviewGenerationPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiBackedSprintReviewGenerationService.class);

    private final SprintReviewAiFacade sprintReviewAiFacade;
    private final PlaceholderSprintReviewGenerationService placeholderSprintReviewGenerationService;
    private final OpenAiProperties openAiProperties;
    private final SprintReviewAiProperties sprintReviewAiProperties;
    private final SprintReviewValidator sprintReviewValidator;

    public AiBackedSprintReviewGenerationService(
            SprintReviewAiFacade sprintReviewAiFacade,
            PlaceholderSprintReviewGenerationService placeholderSprintReviewGenerationService,
            OpenAiProperties openAiProperties,
            SprintReviewAiProperties sprintReviewAiProperties,
            SprintReviewValidator sprintReviewValidator
    ) {
        this.sprintReviewAiFacade = sprintReviewAiFacade;
        this.placeholderSprintReviewGenerationService = placeholderSprintReviewGenerationService;
        this.openAiProperties = openAiProperties;
        this.sprintReviewAiProperties = sprintReviewAiProperties;
        this.sprintReviewValidator = sprintReviewValidator;
    }

    @Override
    public SprintReview generate(SprintContext context, SprintReviewGenerationInput input) {
        if (!openAiProperties.enabled() || !sprintReviewAiProperties.useAiGeneration()) {
            LOGGER.info("sprintreview.generation.path mode=placeholder reason=ai_disabled sprintId={}", context.externalSprintId());
            return placeholderSprintReviewGenerationService.generate(context, input);
        }

        try {
            LOGGER.info(
                    "sprintreview.generation.path mode={} sprintId={} generationSource={} model={}",
                    openAiProperties.mockMode() ? "ai-mock" : "ai-real",
                    context.externalSprintId(),
                    input.generationSource(),
                    openAiProperties.model()
            );
            SprintReviewAiFacade.SprintReviewAiResult aiResult = sprintReviewAiFacade.generate(
                    context,
                    openAiProperties.model(),
                    openAiProperties.temperature(),
                    openAiProperties.maxOutputTokens(),
                    input.audience(),
                    input.tone()
            );

            SprintReview generated = new SprintReview(
                    UUID.randomUUID(),
                    context.workspaceId(),
                    context.externalSprintId(),
                    context.sprintName(),
                    aiResult.summary(),
                    aiResult.themes(),
                    aiResult.highlights(),
                    aiResult.blockers(),
                    aiResult.speakerNotes(),
                    Instant.now(),
                    input.generationSource(),
                    "GENERATED"
            );
            SprintReview validated = sprintReviewValidator.validate(generated);
            LOGGER.info(
                    "sprintreview.generation.ai.completed sprintId={} generationSource={} themes={} highlights={} blockers={} speakerNotes={}",
                    context.externalSprintId(),
                    input.generationSource(),
                    validated.themes() != null ? validated.themes().size() : 0,
                    validated.highlights() != null ? validated.highlights().size() : 0,
                    validated.blockers() != null ? validated.blockers().size() : 0,
                    validated.speakerNotes() != null ? validated.speakerNotes().size() : 0
            );
            return validated;
        } catch (RuntimeException exception) {
            if (sprintReviewAiProperties.fallbackToPlaceholder()) {
                LOGGER.warn(
                        "sprintreview.generation.path mode=placeholder reason=ai_failure sprintId={} generationSource={} model={}",
                        context.externalSprintId(),
                        input.generationSource(),
                        openAiProperties.model(),
                        exception
                );
                return placeholderSprintReviewGenerationService.generate(context, input);
            }
            throw exception;
        }
    }
}

package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.ai.application.SprintReviewAiFacade;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.sprintreview.application.support.PlaceholderSprintReviewGenerationService;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintReviewGenerationInput;
import com.willmear.sprint.sprintreview.domain.port.SprintReviewGenerationPort;
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

    public AiBackedSprintReviewGenerationService(
            SprintReviewAiFacade sprintReviewAiFacade,
            PlaceholderSprintReviewGenerationService placeholderSprintReviewGenerationService,
            OpenAiProperties openAiProperties
    ) {
        this.sprintReviewAiFacade = sprintReviewAiFacade;
        this.placeholderSprintReviewGenerationService = placeholderSprintReviewGenerationService;
        this.openAiProperties = openAiProperties;
    }

    @Override
    public SprintReview generate(SprintContext context, SprintReviewGenerationInput input) {
        if (!openAiProperties.enabled()) {
            LOGGER.info("sprintreview.generation.path mode=placeholder reason=openai_disabled sprintId={}", context.externalSprintId());
            return placeholderSprintReviewGenerationService.generate(context, input);
        }

        try {
            LOGGER.info("sprintreview.generation.path mode=ai sprintId={} generationSource={}", context.externalSprintId(), input.generationSource());
            SprintReviewAiFacade.SprintReviewAiResult aiResult = sprintReviewAiFacade.generate(
                    context,
                    openAiProperties.model(),
                    openAiProperties.temperature(),
                    openAiProperties.maxOutputTokens(),
                    input.audience(),
                    input.tone()
            );

            return new SprintReview(
                    UUID.randomUUID(),
                    context.workspaceId(),
                    context.externalSprintId(),
                    context.sprintName(),
                    aiResult.summary(),
                    aiResult.themes(),
                    placeholderSprintReviewGenerationService.buildHighlights(context),
                    placeholderSprintReviewGenerationService.buildBlockers(context),
                    aiResult.speakerNotes(),
                    Instant.now(),
                    input.generationSource(),
                    "GENERATED"
            );
        } catch (RuntimeException exception) {
            // TODO: Replace broad fallback with policy-driven retries once AI run persistence exists.
            LOGGER.warn("sprintreview.generation.path mode=placeholder reason=ai_failure sprintId={}", context.externalSprintId(), exception);
            return placeholderSprintReviewGenerationService.generate(context, input);
        }
    }
}

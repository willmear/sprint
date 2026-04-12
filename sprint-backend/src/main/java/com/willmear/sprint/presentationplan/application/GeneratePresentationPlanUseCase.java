package com.willmear.sprint.presentationplan.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.model.PresentationPlanAiResponse;
import com.willmear.sprint.ai.model.PresentationPlanBlockAiResponse;
import com.willmear.sprint.ai.model.PresentationPlanSlideAiResponse;
import com.willmear.sprint.ai.parser.PresentationPlanParser;
import com.willmear.sprint.ai.prompt.builder.PresentationPlanPromptBuilder;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.presentationplan.domain.LayoutHint;
import com.willmear.sprint.presentationplan.domain.PlannedSlide;
import com.willmear.sprint.presentationplan.domain.PresentationPlan;
import com.willmear.sprint.presentationplan.domain.SlideBlock;
import com.willmear.sprint.presentationplan.domain.SlideBlockType;
import com.willmear.sprint.presentationplan.domain.SlideIntent;
import com.willmear.sprint.presentationplan.domain.VisualPriority;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GeneratePresentationPlanUseCase {

    private final AiGenerationService aiGenerationService;
    private final PresentationPlanPromptBuilder presentationPlanPromptBuilder;
    private final PresentationPlanParser presentationPlanParser;
    private final OpenAiProperties openAiProperties;

    public GeneratePresentationPlanUseCase(
            AiGenerationService aiGenerationService,
            PresentationPlanPromptBuilder presentationPlanPromptBuilder,
            PresentationPlanParser presentationPlanParser,
            OpenAiProperties openAiProperties
    ) {
        this.aiGenerationService = aiGenerationService;
        this.presentationPlanPromptBuilder = presentationPlanPromptBuilder;
        this.presentationPlanParser = presentationPlanParser;
        this.openAiProperties = openAiProperties;
    }

    public PresentationPlan generate(UUID workspaceId, String referenceType, String referenceId, SprintReview sprintReview) {
        AiGenerationRequest request = new AiGenerationRequest(
                "presentation-plan",
                "presentation-plan",
                openAiProperties.model(),
                presentationPlanPromptBuilder.build(sprintReview, openAiProperties.model()),
                openAiProperties.temperature(),
                openAiProperties.maxOutputTokens(),
                true,
                "presentation-plan"
        );
        AiGenerationResult<PresentationPlanAiResponse> result = aiGenerationService.generate(request, presentationPlanParser::parse);
        return toPresentationPlan(workspaceId, referenceType, referenceId, result.parsedResult(), sprintReview);
    }

    private PresentationPlan toPresentationPlan(
            UUID workspaceId,
            String referenceType,
            String referenceId,
            PresentationPlanAiResponse response,
            SprintReview sprintReview
    ) {
        List<PlannedSlide> slides = response.slides() == null ? List.of() : response.slides().stream()
                .map(this::toPlannedSlide)
                .toList();
        return new PresentationPlan(
                workspaceId,
                referenceType,
                referenceId,
                safe(response.title(), "Sprint Review Deck: " + sprintReview.sprintName()),
                safe(response.subtitle(), sprintReview.summary().overview()),
                reindex(slides),
                Instant.now()
        );
    }

    private List<PlannedSlide> reindex(List<PlannedSlide> slides) {
        return java.util.stream.IntStream.range(0, slides.size())
                .mapToObj(index -> {
                    PlannedSlide slide = slides.get(index);
                    return new PlannedSlide(
                            index,
                            slide.slideIntent(),
                            slide.title(),
                            slide.subtitle(),
                            slide.blocks(),
                            slide.layoutHint(),
                            slide.speakerNotes()
                    );
                })
                .toList();
    }

    private PlannedSlide toPlannedSlide(PresentationPlanSlideAiResponse slide) {
        return new PlannedSlide(
                0,
                parseSlideIntent(slide.slideIntent()),
                safe(slide.title(), "Untitled slide"),
                blankToNull(slide.subtitle()),
                slide.blocks() == null ? List.of() : slide.blocks().stream().map(this::toBlock).toList(),
                parseLayoutHint(slide.layoutHint()),
                blankToNull(slide.speakerNotes())
        );
    }

    private SlideBlock toBlock(PresentationPlanBlockAiResponse block) {
        return new SlideBlock(
                parseBlockType(block.blockType()),
                null,
                block.items() == null ? List.of() : block.items().stream().filter(item -> item != null && !item.isBlank()).map(String::trim).toList(),
                blankToNull(block.text()),
                parseVisualPriority(block.visualPriority())
        );
    }

    private SlideIntent parseSlideIntent(String value) {
        return parseEnum(value, SlideIntent.class, SlideIntent.OVERVIEW);
    }

    private LayoutHint parseLayoutHint(String value) {
        return parseEnum(value, LayoutHint.class, LayoutHint.TITLE_AND_BULLETS);
    }

    private SlideBlockType parseBlockType(String value) {
        return parseEnum(value, SlideBlockType.class, SlideBlockType.BULLETS);
    }

    private VisualPriority parseVisualPriority(String value) {
        return parseEnum(value, VisualPriority.class, VisualPriority.SECONDARY);
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumType, E fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase().replace('-', '_').replace(' ', '_'));
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

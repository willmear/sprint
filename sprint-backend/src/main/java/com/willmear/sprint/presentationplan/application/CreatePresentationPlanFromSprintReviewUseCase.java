package com.willmear.sprint.presentationplan.application;

import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.presentationplan.domain.PresentationPlan;
import com.willmear.sprint.presentationplan.mapper.SprintReviewToPresentationPlanMapper;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreatePresentationPlanFromSprintReviewUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePresentationPlanFromSprintReviewUseCase.class);

    private final SprintReviewToPresentationPlanMapper sprintReviewToPresentationPlanMapper;
    private final GeneratePresentationPlanUseCase generatePresentationPlanUseCase;
    private final OpenAiProperties openAiProperties;
    private final PresentationAiProperties presentationAiProperties;

    public CreatePresentationPlanFromSprintReviewUseCase(
            SprintReviewToPresentationPlanMapper sprintReviewToPresentationPlanMapper,
            GeneratePresentationPlanUseCase generatePresentationPlanUseCase,
            OpenAiProperties openAiProperties,
            PresentationAiProperties presentationAiProperties
    ) {
        this.sprintReviewToPresentationPlanMapper = sprintReviewToPresentationPlanMapper;
        this.generatePresentationPlanUseCase = generatePresentationPlanUseCase;
        this.openAiProperties = openAiProperties;
        this.presentationAiProperties = presentationAiProperties;
    }

    public PresentationPlan create(UUID workspaceId, String referenceType, String referenceId, SprintReview sprintReview) {
        if (!openAiProperties.enabled() || !presentationAiProperties.useAiPlanning()) {
            LOGGER.info("presentationplan.generation.path mode=deterministic reason=ai_disabled sprintId={}", sprintReview.externalSprintId());
            return deterministicPlan(workspaceId, referenceType, referenceId, sprintReview);
        }

        try {
            LOGGER.info(
                    "presentationplan.generation.path mode={} sprintId={} model={}",
                    openAiProperties.mockMode() ? "ai-mock" : "ai-real",
                    sprintReview.externalSprintId(),
                    openAiProperties.model()
            );
            PresentationPlan plan = generatePresentationPlanUseCase.generate(workspaceId, referenceType, referenceId, sprintReview);
            if (plan.slides() == null || plan.slides().isEmpty()) {
                throw new IllegalStateException("generated_plan_empty");
            }
            return plan;
        } catch (RuntimeException exception) {
            if (presentationAiProperties.fallbackToDeterministic()) {
                LOGGER.warn(
                        "presentationplan.generation.path mode=deterministic reason=ai_failure sprintId={} model={}",
                        sprintReview.externalSprintId(),
                        openAiProperties.model(),
                        exception
                );
                return deterministicPlan(workspaceId, referenceType, referenceId, sprintReview);
            }
            throw exception;
        }
    }

    private PresentationPlan deterministicPlan(UUID workspaceId, String referenceType, String referenceId, SprintReview sprintReview) {
        return sprintReviewToPresentationPlanMapper.toPlan(workspaceId, referenceType, referenceId, sprintReview);
    }
}

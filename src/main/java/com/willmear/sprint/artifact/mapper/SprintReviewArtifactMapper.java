package com.willmear.sprint.artifact.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.artifact.application.support.SprintReviewMarkdownRenderer;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactStatus;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.common.exception.ArtifactPersistenceException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewArtifactMapper {

    public static final String SPRINT_REFERENCE_TYPE = "SPRINT";
    private static final String GENERATOR_VERSION = "sprint-review-v1";

    private final ObjectMapper objectMapper;
    private final SprintReviewMarkdownRenderer sprintReviewMarkdownRenderer;

    public SprintReviewArtifactMapper(
            ObjectMapper objectMapper,
            SprintReviewMarkdownRenderer sprintReviewMarkdownRenderer
    ) {
        this.objectMapper = objectMapper;
        this.sprintReviewMarkdownRenderer = sprintReviewMarkdownRenderer;
    }

    public Artifact toArtifact(SprintReview sprintReview) {
        Instant generatedAt = sprintReview.generatedAt() != null ? sprintReview.generatedAt() : Instant.now();
        return new Artifact(
                UUID.randomUUID(),
                sprintReview.workspaceId(),
                ArtifactType.SPRINT_REVIEW,
                ArtifactStatus.GENERATED,
                SPRINT_REFERENCE_TYPE,
                sprintReview.externalSprintId().toString(),
                objectMapper.valueToTree(sprintReview),
                sprintReviewMarkdownRenderer.render(sprintReview),
                sprintReview.summary().title(),
                sprintReview.summary().overview(),
                sprintReview.generationSource(),
                GENERATOR_VERSION,
                generatedAt,
                null,
                null
        );
    }

    public SprintReview toSprintReview(Artifact artifact) {
        try {
            return objectMapper.treeToValue(artifact.structuredContent(), SprintReview.class);
        } catch (RuntimeException | com.fasterxml.jackson.core.JsonProcessingException exception) {
            throw new ArtifactPersistenceException("Failed to deserialize sprint review artifact " + artifact.id() + ".", exception);
        }
    }
}

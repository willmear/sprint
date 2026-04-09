package com.willmear.sprint.artifact.mapper;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.artifact.application.support.SprintReviewMarkdownRenderer;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactStatus;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.entity.ArtifactEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtifactMapperAndResponseMapperTest {

    private final ArtifactMapper artifactMapper = new ArtifactMapper();
    private final ArtifactResponseMapper artifactResponseMapper = new ArtifactResponseMapper();

    @Test
    void shouldRoundTripBetweenDomainAndEntity() {
        Artifact artifact = TestSprintReviewFactory.artifact();

        ArtifactEntity entity = artifactMapper.toEntity(artifact);
        Artifact restored = artifactMapper.toDomain(entity);

        assertThat(entity.getArtifactType()).isEqualTo(ArtifactType.SPRINT_REVIEW);
        assertThat(restored).isEqualTo(artifact);
    }

    @Test
    void shouldTreatUnsavedArtifactsAsNewEntities() {
        Artifact unsavedArtifact = new Artifact(
                java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(),
                ArtifactType.SPRINT_REVIEW,
                ArtifactStatus.GENERATED,
                "SPRINT",
                "77",
                com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode(),
                "# md",
                "title",
                "summary",
                "AI",
                "v1",
                Instant.now(),
                null,
                null
        );

        ArtifactEntity entity = artifactMapper.toEntity(unsavedArtifact);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void shouldMapArtifactResponses() {
        Artifact artifact = TestSprintReviewFactory.artifact();

        assertThat(artifactResponseMapper.toResponse(artifact).artifactType()).isEqualTo("SPRINT_REVIEW");
        assertThat(artifactResponseMapper.toSummaryResponse(artifact).status()).isEqualTo("GENERATED");
    }

    @Test
    void shouldRenderMarkdownIncludingIssueLists() {
        SprintReviewMarkdownRenderer renderer = new SprintReviewMarkdownRenderer();

        String markdown = renderer.render(TestSprintReviewFactory.reviewWithHighlight());

        assertThat(markdown).contains("# Sprint Review", "## Highlights", "SPR-1");
    }
}

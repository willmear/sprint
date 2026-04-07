package com.willmear.sprint.artifact.mapper;

import com.fasterxml.jackson.databind.node.TextNode;
import com.willmear.sprint.artifact.application.support.SprintReviewMarkdownRenderer;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.common.exception.ArtifactPersistenceException;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SprintReviewArtifactMapperTest {

    private final SprintReviewMarkdownRenderer renderer = new SprintReviewMarkdownRenderer();
    private final SprintReviewArtifactMapper mapper = new SprintReviewArtifactMapper(new com.fasterxml.jackson.databind.ObjectMapper(), renderer);

    @Test
    void shouldConvertSprintReviewToArtifactAndBack() {
        SprintReview sprintReview = sprintReview();

        Artifact artifact = mapper.toArtifact(sprintReview);
        SprintReview restored = mapper.toSprintReview(artifact);

        assertThat(artifact.artifactType()).isEqualTo(ArtifactType.SPRINT_REVIEW);
        assertThat(artifact.referenceType()).isEqualTo(SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE);
        assertThat(artifact.referenceId()).isEqualTo("55");
        assertThat(artifact.renderedMarkdown()).contains("# Sprint Review: Sprint 55", "## Themes", "## Highlights");
        assertThat(restored.sprintName()).isEqualTo("Sprint 55");
        assertThat(restored.summary().overview()).isEqualTo("Overview");
    }

    @Test
    void shouldThrowWhenStructuredContentCannotBeDeserialized() {
        Artifact artifact = new Artifact(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ArtifactType.SPRINT_REVIEW,
                com.willmear.sprint.artifact.domain.ArtifactStatus.GENERATED,
                "SPRINT",
                "55",
                TextNode.valueOf("not-json-object"),
                "markdown",
                "title",
                "summary",
                "AI",
                "v1",
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        assertThatThrownBy(() -> mapper.toSprintReview(artifact))
                .isInstanceOf(ArtifactPersistenceException.class);
    }

    @Test
    void markdownRendererShouldRenderEmptySectionsDeterministically() {
        SprintReview sprintReview = new SprintReview(
                UUID.randomUUID(),
                UUID.randomUUID(),
                55L,
                "Sprint 55",
                new SprintSummary("Sprint Review: Sprint 55", "Overview", null, null, null),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                Instant.now(),
                "DIRECT",
                "GENERATED"
        );

        String markdown = renderer.render(sprintReview);

        assertThat(markdown).contains("## Themes", "- None recorded.", "## Speaker Notes");
    }

    private SprintReview sprintReview() {
        return new SprintReview(
                UUID.randomUUID(),
                UUID.randomUUID(),
                55L,
                "Sprint 55",
                new SprintSummary("Sprint Review: Sprint 55", "Overview", "Delivery", "Quality", "Outcome"),
                List.of(new SprintTheme("Delivery", "Customer delivery", List.of("SPR-1"))),
                List.of(new SprintHighlight("SPR-1 - Feature", "Delivered feature", List.of("SPR-1"), "FEATURE")),
                List.of(new SprintBlocker("Blocked item", "Dependency waiting", List.of("SPR-2"), "MEDIUM")),
                List.of(new SpeakerNote("Intro", "Open with goal", 1)),
                null,
                "DIRECT",
                "GENERATED"
        );
    }
}

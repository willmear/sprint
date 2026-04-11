package com.willmear.sprint.export.renderer;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownExportRendererTest {

    private final MarkdownExportRenderer renderer = new MarkdownExportRenderer();

    @Test
    void shouldRenderPolishedMarkdownSections() {
        ExportPayload exportPayload = renderer.render(TestSprintReviewFactory.reviewWithHighlight());

        assertThat(exportPayload.format()).isEqualTo(ExportFormat.MARKDOWN);
        assertThat(exportPayload.fileName()).endsWith(".md");
        assertThat(exportPayload.textContent()).contains(
                "# Sprint Review: Sprint 42",
                "## Overview",
                "## Themes",
                "## Highlights",
                "## Blockers",
                "## Speaker Notes"
        );
    }
}

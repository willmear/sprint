package com.willmear.sprint.export.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.export.mapper.PresentationOutlineResponseMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PresentationOutlineExportRendererTest {

    private final PresentationOutlineExportRenderer renderer = new PresentationOutlineExportRenderer(
            new ObjectMapper(),
            new PresentationOutlineResponseMapper()
    );

    @Test
    void shouldRenderStructuredSlideOutline() {
        ExportPayload exportPayload = renderer.render(TestSprintReviewFactory.reviewWithHighlight());

        assertThat(exportPayload.format()).isEqualTo(ExportFormat.PRESENTATION_OUTLINE);
        assertThat(exportPayload.fileName()).endsWith("-outline.json");
        assertThat(exportPayload.structuredContent()).isNotNull();
        assertThat(exportPayload.structuredContent().path("slides")).hasSize(6);
        assertThat(exportPayload.textContent()).contains("Slide 1 - Sprint Review: Sprint 42", "Slide 6 - Wrap-up");
    }
}

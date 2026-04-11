package com.willmear.sprint.export.renderer;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpeakerNotesExportRendererTest {

    private final SpeakerNotesExportRenderer renderer = new SpeakerNotesExportRenderer();

    @Test
    void shouldRenderCopyReadySpeakerNotes() {
        ExportPayload exportPayload = renderer.render(TestSprintReviewFactory.reviewWithHighlight());

        assertThat(exportPayload.format()).isEqualTo(ExportFormat.SPEAKER_NOTES);
        assertThat(exportPayload.fileName()).endsWith("-speaker-notes.txt");
        assertThat(exportPayload.textContent()).contains("Intro", "Delivery highlights", "Risks and blockers", "Closing summary");
    }
}

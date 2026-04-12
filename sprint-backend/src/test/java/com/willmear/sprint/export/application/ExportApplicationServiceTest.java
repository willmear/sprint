package com.willmear.sprint.export.application;

import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.export.domain.BinaryExportPayload;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportApplicationServiceTest {

    private final ExportArtifactUseCase exportArtifactUseCase = mock(ExportArtifactUseCase.class);
    private final ExportLatestSprintReviewUseCase exportLatestSprintReviewUseCase = mock(ExportLatestSprintReviewUseCase.class);
    private final ExportPresentationDeckAsPowerPointUseCase exportPresentationDeckAsPowerPointUseCase = mock(ExportPresentationDeckAsPowerPointUseCase.class);
    private final ExportLatestDeckForSprintUseCase exportLatestDeckForSprintUseCase = mock(ExportLatestDeckForSprintUseCase.class);
    private final ExportApplicationService service = new ExportApplicationService(
            exportArtifactUseCase,
            exportLatestSprintReviewUseCase,
            exportPresentationDeckAsPowerPointUseCase,
            exportLatestDeckForSprintUseCase
    );

    @Test
    void shouldExportLatestSprintReview() {
        UUID workspaceId = UUID.randomUUID();
        ExportPayload expected = new ExportPayload(ExportFormat.MARKDOWN, "file.md", "text/markdown", "text", null, Instant.now());
        when(exportLatestSprintReviewUseCase.export(workspaceId, 42L, ExportFormat.MARKDOWN)).thenReturn(expected);

        ExportPayload result = service.exportLatestSprintReview(workspaceId, 42L, ExportFormat.MARKDOWN);

        assertThat(result).isEqualTo(expected);
        verify(exportLatestSprintReviewUseCase).export(workspaceId, 42L, ExportFormat.MARKDOWN);
    }

    @Test
    void shouldExportArtifact() {
        UUID artifactId = UUID.randomUUID();
        ExportPayload expected = new ExportPayload(ExportFormat.SPEAKER_NOTES, "file.txt", "text/plain", "text", null, Instant.now());
        when(exportArtifactUseCase.export(artifactId, ExportFormat.SPEAKER_NOTES)).thenReturn(expected);

        ExportPayload result = service.exportArtifact(artifactId, ExportFormat.SPEAKER_NOTES);

        assertThat(result).isEqualTo(expected);
        verify(exportArtifactUseCase).export(artifactId, ExportFormat.SPEAKER_NOTES);
    }

    @Test
    void shouldExportPresentationDeckAsPowerPoint() {
        UUID workspaceId = UUID.randomUUID();
        UUID deckId = UUID.randomUUID();
        BinaryExportPayload expected = new BinaryExportPayload(
                ExportFormat.POWERPOINT,
                "deck.pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                new byte[]{1, 2, 3},
                Instant.now()
        );
        when(exportPresentationDeckAsPowerPointUseCase.export(workspaceId, deckId)).thenReturn(expected);

        BinaryExportPayload result = service.exportPresentationDeckAsPowerPoint(workspaceId, deckId);

        assertThat(result).isEqualTo(expected);
        verify(exportPresentationDeckAsPowerPointUseCase).export(workspaceId, deckId);
    }
}

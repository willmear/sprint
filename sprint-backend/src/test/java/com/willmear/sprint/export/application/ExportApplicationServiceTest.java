package com.willmear.sprint.export.application;

import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
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
    private final ExportApplicationService service = new ExportApplicationService(exportArtifactUseCase, exportLatestSprintReviewUseCase);

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
}

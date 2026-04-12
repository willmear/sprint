package com.willmear.sprint.export.application;

import com.willmear.sprint.export.api.ExportService;
import com.willmear.sprint.export.domain.BinaryExportPayload;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExportApplicationService implements ExportService {

    private final ExportArtifactUseCase exportArtifactUseCase;
    private final ExportLatestSprintReviewUseCase exportLatestSprintReviewUseCase;
    private final ExportPresentationDeckAsPowerPointUseCase exportPresentationDeckAsPowerPointUseCase;
    private final ExportLatestDeckForSprintUseCase exportLatestDeckForSprintUseCase;

    public ExportApplicationService(
            ExportArtifactUseCase exportArtifactUseCase,
            ExportLatestSprintReviewUseCase exportLatestSprintReviewUseCase,
            ExportPresentationDeckAsPowerPointUseCase exportPresentationDeckAsPowerPointUseCase,
            ExportLatestDeckForSprintUseCase exportLatestDeckForSprintUseCase
    ) {
        this.exportArtifactUseCase = exportArtifactUseCase;
        this.exportLatestSprintReviewUseCase = exportLatestSprintReviewUseCase;
        this.exportPresentationDeckAsPowerPointUseCase = exportPresentationDeckAsPowerPointUseCase;
        this.exportLatestDeckForSprintUseCase = exportLatestDeckForSprintUseCase;
    }

    @Override
    public ExportPayload exportLatestSprintReview(UUID workspaceId, Long sprintId, ExportFormat format) {
        return exportLatestSprintReviewUseCase.export(workspaceId, sprintId, format);
    }

    @Override
    public ExportPayload exportArtifact(UUID artifactId, ExportFormat format) {
        return exportArtifactUseCase.export(artifactId, format);
    }

    @Override
    public BinaryExportPayload exportPresentationDeckAsPowerPoint(UUID workspaceId, UUID deckId) {
        return exportPresentationDeckAsPowerPointUseCase.export(workspaceId, deckId);
    }

    @Override
    public BinaryExportPayload exportLatestDeckForSprintAsPowerPoint(UUID workspaceId, Long sprintId) {
        return exportLatestDeckForSprintUseCase.export(workspaceId, sprintId);
    }
}

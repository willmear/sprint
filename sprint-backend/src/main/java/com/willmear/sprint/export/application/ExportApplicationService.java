package com.willmear.sprint.export.application;

import com.willmear.sprint.export.api.ExportService;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExportApplicationService implements ExportService {

    private final ExportArtifactUseCase exportArtifactUseCase;
    private final ExportLatestSprintReviewUseCase exportLatestSprintReviewUseCase;

    public ExportApplicationService(
            ExportArtifactUseCase exportArtifactUseCase,
            ExportLatestSprintReviewUseCase exportLatestSprintReviewUseCase
    ) {
        this.exportArtifactUseCase = exportArtifactUseCase;
        this.exportLatestSprintReviewUseCase = exportLatestSprintReviewUseCase;
    }

    @Override
    public ExportPayload exportLatestSprintReview(UUID workspaceId, Long sprintId, ExportFormat format) {
        return exportLatestSprintReviewUseCase.export(workspaceId, sprintId, format);
    }

    @Override
    public ExportPayload exportArtifact(UUID artifactId, ExportFormat format) {
        return exportArtifactUseCase.export(artifactId, format);
    }
}

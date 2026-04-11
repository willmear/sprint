package com.willmear.sprint.export.application;

import com.willmear.sprint.artifact.api.ArtifactService;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.export.mapper.ArtifactToSprintReviewMapper;
import com.willmear.sprint.export.renderer.ExportRendererRegistry;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExportLatestSprintReviewUseCase {

    private final ArtifactService artifactService;
    private final ArtifactToSprintReviewMapper artifactToSprintReviewMapper;
    private final ExportRendererRegistry exportRendererRegistry;

    public ExportLatestSprintReviewUseCase(
            ArtifactService artifactService,
            ArtifactToSprintReviewMapper artifactToSprintReviewMapper,
            ExportRendererRegistry exportRendererRegistry
    ) {
        this.artifactService = artifactService;
        this.artifactToSprintReviewMapper = artifactToSprintReviewMapper;
        this.exportRendererRegistry = exportRendererRegistry;
    }

    public ExportPayload export(UUID workspaceId, Long sprintId, ExportFormat format) {
        Artifact artifact = artifactService.getLatest(
                workspaceId,
                ArtifactType.SPRINT_REVIEW,
                SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE,
                sprintId.toString()
        );
        return exportRendererRegistry.getRenderer(format)
                .render(artifactToSprintReviewMapper.toSprintReview(artifact));
    }
}

package com.willmear.sprint.export.application;

import com.willmear.sprint.artifact.api.ArtifactService;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.export.mapper.ArtifactToSprintReviewMapper;
import com.willmear.sprint.export.renderer.ExportRendererRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExportArtifactUseCase {

    private final ArtifactService artifactService;
    private final ArtifactToSprintReviewMapper artifactToSprintReviewMapper;
    private final ExportRendererRegistry exportRendererRegistry;

    public ExportArtifactUseCase(
            ArtifactService artifactService,
            ArtifactToSprintReviewMapper artifactToSprintReviewMapper,
            ExportRendererRegistry exportRendererRegistry
    ) {
        this.artifactService = artifactService;
        this.artifactToSprintReviewMapper = artifactToSprintReviewMapper;
        this.exportRendererRegistry = exportRendererRegistry;
    }

    public ExportPayload export(UUID artifactId, ExportFormat format) {
        Artifact artifact = artifactService.get(artifactId);
        return exportRendererRegistry.getRenderer(format)
                .render(artifactToSprintReviewMapper.toSprintReview(artifact));
    }
}

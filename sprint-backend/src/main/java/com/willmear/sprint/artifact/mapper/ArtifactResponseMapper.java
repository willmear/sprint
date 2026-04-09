package com.willmear.sprint.artifact.mapper;

import com.willmear.sprint.artifact.api.response.ArtifactResponse;
import com.willmear.sprint.artifact.api.response.ArtifactSummaryResponse;
import com.willmear.sprint.artifact.domain.Artifact;
import org.springframework.stereotype.Component;

@Component
public class ArtifactResponseMapper {

    public ArtifactResponse toResponse(Artifact artifact) {
        return new ArtifactResponse(
                artifact.id(),
                artifact.workspaceId(),
                artifact.artifactType().name(),
                artifact.status().name(),
                artifact.referenceType(),
                artifact.referenceId(),
                artifact.structuredContent(),
                artifact.renderedMarkdown(),
                artifact.title(),
                artifact.summary(),
                artifact.generatorType(),
                artifact.generatorVersion(),
                artifact.generatedAt(),
                artifact.createdAt(),
                artifact.updatedAt()
        );
    }

    public ArtifactSummaryResponse toSummaryResponse(Artifact artifact) {
        return new ArtifactSummaryResponse(
                artifact.id(),
                artifact.workspaceId(),
                artifact.artifactType().name(),
                artifact.status().name(),
                artifact.referenceType(),
                artifact.referenceId(),
                artifact.title(),
                artifact.summary(),
                artifact.generatorType(),
                artifact.generatedAt(),
                artifact.createdAt()
        );
    }
}

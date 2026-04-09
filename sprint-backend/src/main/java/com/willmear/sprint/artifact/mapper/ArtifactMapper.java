package com.willmear.sprint.artifact.mapper;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.entity.ArtifactEntity;
import org.springframework.stereotype.Component;

@Component
public class ArtifactMapper {

    public ArtifactEntity toEntity(Artifact artifact) {
        ArtifactEntity entity = new ArtifactEntity();
        if (isPersistedArtifact(artifact)) {
            entity.setId(artifact.id());
            entity.setCreatedAt(artifact.createdAt());
            entity.setUpdatedAt(artifact.updatedAt());
        }
        entity.setWorkspaceId(artifact.workspaceId());
        entity.setArtifactType(artifact.artifactType());
        entity.setStatus(artifact.status());
        entity.setReferenceType(artifact.referenceType());
        entity.setReferenceId(artifact.referenceId());
        entity.setStructuredContent(artifact.structuredContent());
        entity.setRenderedMarkdown(artifact.renderedMarkdown());
        entity.setTitle(artifact.title());
        entity.setSummary(artifact.summary());
        entity.setGeneratorType(artifact.generatorType());
        entity.setGeneratorVersion(artifact.generatorVersion());
        entity.setGeneratedAt(artifact.generatedAt());
        return entity;
    }

    private boolean isPersistedArtifact(Artifact artifact) {
        return artifact.id() != null && artifact.createdAt() != null && artifact.updatedAt() != null;
    }

    public Artifact toDomain(ArtifactEntity entity) {
        return new Artifact(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getArtifactType(),
                entity.getStatus(),
                entity.getReferenceType(),
                entity.getReferenceId(),
                entity.getStructuredContent(),
                entity.getRenderedMarkdown(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getGeneratorType(),
                entity.getGeneratorVersion(),
                entity.getGeneratedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListArtifactsUseCase {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;
    private final WorkspaceService workspaceService;

    public ListArtifactsUseCase(
            ArtifactRepository artifactRepository,
            ArtifactMapper artifactMapper,
            WorkspaceService workspaceService
    ) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
        this.workspaceService = workspaceService;
    }

    public List<Artifact> list(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId) {
        workspaceService.getWorkspace(workspaceId);
        if (artifactType != null && referenceType != null && referenceId != null) {
            return artifactRepository
                    .findByWorkspaceIdAndArtifactTypeAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
                            workspaceId,
                            artifactType,
                            referenceType,
                            referenceId
                    )
                    .stream()
                    .map(artifactMapper::toDomain)
                    .toList();
        }
        if (referenceType != null && referenceId != null) {
            return artifactRepository
                    .findByWorkspaceIdAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
                            workspaceId,
                            referenceType,
                            referenceId
                    )
                    .stream()
                    .map(artifactMapper::toDomain)
                    .toList();
        }
        if (artifactType != null) {
            return artifactRepository
                    .findByWorkspaceIdAndArtifactTypeOrderByGeneratedAtDescCreatedAtDesc(workspaceId, artifactType)
                    .stream()
                    .map(artifactMapper::toDomain)
                    .toList();
        }
        return artifactRepository.findByWorkspaceIdOrderByGeneratedAtDescCreatedAtDesc(workspaceId).stream()
                .map(artifactMapper::toDomain)
                .toList();
    }
}

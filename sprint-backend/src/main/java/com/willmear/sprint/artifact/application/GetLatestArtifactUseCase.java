package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.common.exception.LatestArtifactNotFoundException;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetLatestArtifactUseCase {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;
    private final WorkspaceService workspaceService;

    public GetLatestArtifactUseCase(
            ArtifactRepository artifactRepository,
            ArtifactMapper artifactMapper,
            WorkspaceService workspaceService
    ) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
        this.workspaceService = workspaceService;
    }

    public Artifact get(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId) {
        workspaceService.getWorkspace(workspaceId);
        return artifactRepository
                .findFirstByWorkspaceIdAndArtifactTypeAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
                        workspaceId,
                        artifactType,
                        referenceType,
                        referenceId
                )
                .map(artifactMapper::toDomain)
                .orElseThrow(() -> new LatestArtifactNotFoundException(workspaceId, artifactType, referenceType, referenceId));
    }
}

package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.common.exception.ArtifactNotFoundException;
import com.willmear.sprint.workspace.api.WorkspaceService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetArtifactUseCase {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;
    private final WorkspaceService workspaceService;

    public GetArtifactUseCase(
            ArtifactRepository artifactRepository,
            ArtifactMapper artifactMapper,
            WorkspaceService workspaceService
    ) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
        this.workspaceService = workspaceService;
    }

    public Artifact get(UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .map(artifactMapper::toDomain)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
        workspaceService.getWorkspace(artifact.workspaceId());
        return artifact;
    }
}

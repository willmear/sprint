package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.common.exception.LatestArtifactNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetLatestArtifactUseCase {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;

    public GetLatestArtifactUseCase(ArtifactRepository artifactRepository, ArtifactMapper artifactMapper) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
    }

    public Artifact get(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId) {
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

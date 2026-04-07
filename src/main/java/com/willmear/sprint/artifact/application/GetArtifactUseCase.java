package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.common.exception.ArtifactNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetArtifactUseCase {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;

    public GetArtifactUseCase(ArtifactRepository artifactRepository, ArtifactMapper artifactMapper) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
    }

    public Artifact get(UUID artifactId) {
        return artifactRepository.findById(artifactId)
                .map(artifactMapper::toDomain)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }
}

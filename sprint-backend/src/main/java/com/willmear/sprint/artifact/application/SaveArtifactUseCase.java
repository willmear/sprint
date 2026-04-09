package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.common.exception.ArtifactPersistenceException;
import org.springframework.stereotype.Service;

@Service
public class SaveArtifactUseCase {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;

    public SaveArtifactUseCase(ArtifactRepository artifactRepository, ArtifactMapper artifactMapper) {
        this.artifactRepository = artifactRepository;
        this.artifactMapper = artifactMapper;
    }

    public Artifact save(Artifact artifact) {
        try {
            return artifactMapper.toDomain(artifactRepository.save(artifactMapper.toEntity(artifact)));
        } catch (RuntimeException exception) {
            throw new ArtifactPersistenceException("Failed to persist artifact.", exception);
        }
    }
}

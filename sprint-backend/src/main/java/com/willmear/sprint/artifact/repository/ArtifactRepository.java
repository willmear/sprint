package com.willmear.sprint.artifact.repository;

import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.entity.ArtifactEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtifactRepository extends JpaRepository<ArtifactEntity, UUID> {

    Optional<ArtifactEntity> findByIdAndWorkspaceId(UUID artifactId, UUID workspaceId);

    List<ArtifactEntity> findByWorkspaceIdOrderByGeneratedAtDescCreatedAtDesc(UUID workspaceId);

    List<ArtifactEntity> findByWorkspaceIdAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
            UUID workspaceId,
            String referenceType,
            String referenceId
    );

    List<ArtifactEntity> findByWorkspaceIdAndArtifactTypeOrderByGeneratedAtDescCreatedAtDesc(
            UUID workspaceId,
            ArtifactType artifactType
    );

    Optional<ArtifactEntity> findFirstByWorkspaceIdAndArtifactTypeAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
            UUID workspaceId,
            ArtifactType artifactType,
            String referenceType,
            String referenceId
    );

    List<ArtifactEntity> findByWorkspaceIdAndArtifactTypeAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
            UUID workspaceId,
            ArtifactType artifactType,
            String referenceType,
            String referenceId
    );
}

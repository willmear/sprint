package com.willmear.sprint.artifact.api;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import java.util.List;
import java.util.UUID;

public interface ArtifactService {

    Artifact save(Artifact artifact);

    Artifact get(UUID artifactId);

    Artifact getLatest(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId);

    List<Artifact> list(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId);
}

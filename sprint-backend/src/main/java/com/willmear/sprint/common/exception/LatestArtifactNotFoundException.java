package com.willmear.sprint.common.exception;

import com.willmear.sprint.artifact.domain.ArtifactType;
import java.util.UUID;

public class LatestArtifactNotFoundException extends NotFoundException {

    public LatestArtifactNotFoundException(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId) {
        super("No artifact found for workspace " + workspaceId
                + ", artifactType=" + artifactType
                + ", referenceType=" + referenceType
                + ", referenceId=" + referenceId + ".");
    }
}

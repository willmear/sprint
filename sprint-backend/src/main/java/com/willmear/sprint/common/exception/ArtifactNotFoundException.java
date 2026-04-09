package com.willmear.sprint.common.exception;

import java.util.UUID;

public class ArtifactNotFoundException extends NotFoundException {

    public ArtifactNotFoundException(UUID artifactId) {
        super("Artifact " + artifactId + " was not found.");
    }
}

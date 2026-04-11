package com.willmear.sprint.presentation.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PresentationDeck(
        UUID id,
        UUID workspaceId,
        String referenceType,
        String referenceId,
        String title,
        String subtitle,
        DeckStatus status,
        List<PresentationSlide> slides,
        UUID sourceArtifactId,
        Instant createdAt,
        Instant updatedAt
) {
}

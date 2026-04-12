package com.willmear.sprint.presentation.api.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PresentationDeckResponse(
        UUID id,
        UUID workspaceId,
        String referenceType,
        String referenceId,
        String title,
        String subtitle,
        String themeId,
        String themeDisplayName,
        PresentationThemeSummaryResponse theme,
        String status,
        UUID sourceArtifactId,
        List<PresentationSlideResponse> slides,
        Instant createdAt,
        Instant updatedAt
) {
}

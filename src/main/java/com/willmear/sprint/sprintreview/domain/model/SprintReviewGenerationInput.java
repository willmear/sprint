package com.willmear.sprint.sprintreview.domain.model;

import java.util.UUID;

public record SprintReviewGenerationInput(
        UUID workspaceId,
        Long externalSprintId,
        boolean includeComments,
        boolean includeChangelog,
        String audience,
        String tone,
        String generationSource
) {
}

package com.willmear.sprint.sprintreview.application;

import java.util.UUID;

public record SprintReviewJobPayload(
        UUID workspaceId,
        Long externalSprintId,
        boolean includeComments,
        boolean includeChangelog,
        boolean forceRegenerate,
        String audience,
        String tone
) {
}

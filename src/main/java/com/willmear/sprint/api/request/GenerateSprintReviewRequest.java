package com.willmear.sprint.api.request;

public record GenerateSprintReviewRequest(
        boolean includeComments,
        boolean includeChangelog,
        boolean forceRegenerate,
        String audience,
        String tone
) {
}

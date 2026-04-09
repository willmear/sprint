package com.willmear.sprint.api.request;

public record GenerateSprintReviewRequest(
        Boolean includeComments,
        Boolean includeChangelog,
        Boolean forceRegenerate,
        String audience,
        String tone
) {
}

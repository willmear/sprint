package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sprint-review.ai")
public record SprintReviewAiProperties(
        boolean useAiGeneration,
        boolean fallbackToPlaceholder,
        int maxIssuesInPrompt,
        int maxCommentsPerIssue,
        int maxDescriptionChars,
        int maxCommentChars
) {
}

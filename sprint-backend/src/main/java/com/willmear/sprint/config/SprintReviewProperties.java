package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sprint-review")
public record SprintReviewProperties(
        boolean includeCommentsByDefault,
        boolean includeChangelogByDefault,
        int maxHighlights,
        int maxThemes,
        boolean enableJobEntrypoint
) {
}

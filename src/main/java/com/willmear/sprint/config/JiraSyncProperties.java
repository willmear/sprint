package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jira.sync")
public record JiraSyncProperties(
        boolean fetchComments,
        boolean fetchChangelog
) {
}

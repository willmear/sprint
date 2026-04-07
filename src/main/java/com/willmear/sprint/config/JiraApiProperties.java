package com.willmear.sprint.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jira.api")
public record JiraApiProperties(
        String defaultBaseUrl,
        Duration connectTimeout,
        Duration readTimeout
) {
}

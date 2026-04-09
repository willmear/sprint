package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jira.oauth")
public record JiraOAuthProperties(
        String clientId,
        String clientSecret,
        String authorizationUrl,
        String tokenUrl,
        String redirectUri
) {
}

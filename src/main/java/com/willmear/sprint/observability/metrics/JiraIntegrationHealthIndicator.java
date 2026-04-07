package com.willmear.sprint.observability.metrics;

import com.willmear.sprint.config.JiraOAuthProperties;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class JiraIntegrationHealthIndicator implements HealthIndicator {

    private final JiraOAuthProperties jiraOAuthProperties;

    public JiraIntegrationHealthIndicator(JiraOAuthProperties jiraOAuthProperties) {
        this.jiraOAuthProperties = jiraOAuthProperties;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("oauthAuthorizationUrlConfigured", jiraOAuthProperties.authorizationUrl() != null)
                .withDetail("oauthRedirectUriConfigured", jiraOAuthProperties.redirectUri() != null)
                .build();
    }
}

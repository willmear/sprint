package com.willmear.sprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JiraApiProperties.class, JiraOAuthProperties.class, JiraSyncProperties.class})
public class JiraConfig {
    // TODO: Register Jira HTTP clients, OAuth support, and rate limit handling.
}

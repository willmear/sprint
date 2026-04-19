package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.credits")
public record CreditsProperties(
        boolean enabled,
        int dailyGenerationLimit
) {
}

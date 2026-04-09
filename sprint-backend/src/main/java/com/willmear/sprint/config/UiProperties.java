package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ui")
public record UiProperties(
        String allowedOrigin
) {
}

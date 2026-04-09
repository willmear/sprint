package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.observability")
public record ObservabilityProperties(
        boolean httpRequestLoggingEnabled,
        String correlationHeaderName,
        boolean logRequestStart,
        boolean logRequestEnd,
        boolean metricsEnabled
) {
}

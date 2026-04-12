package com.willmear.sprint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.presentation.ai")
public record PresentationAiProperties(
        boolean useAiPlanning,
        boolean fallbackToDeterministic
) {
}

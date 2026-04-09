package com.willmear.sprint.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.openai")
public record OpenAiProperties(
        boolean enabled,
        boolean mockMode,
        String apiKey,
        String baseUrl,
        String chatPath,
        String embeddingsPath,
        String model,
        String embeddingModel,
        Duration timeout,
        Integer maxOutputTokens,
        Double temperature
) {
}

package com.willmear.sprint.observability.metrics;

import com.willmear.sprint.config.OpenAiProperties;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class OpenAiHealthIndicator implements HealthIndicator {

    private final OpenAiProperties openAiProperties;

    public OpenAiHealthIndicator(OpenAiProperties openAiProperties) {
        this.openAiProperties = openAiProperties;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("enabled", openAiProperties.enabled())
                .withDetail("mockMode", openAiProperties.mockMode())
                .build();
    }
}

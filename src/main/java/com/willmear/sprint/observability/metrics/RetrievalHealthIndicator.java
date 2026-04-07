package com.willmear.sprint.observability.metrics;

import com.willmear.sprint.config.RetrievalProperties;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class RetrievalHealthIndicator implements HealthIndicator {

    private final RetrievalProperties retrievalProperties;

    public RetrievalHealthIndicator(RetrievalProperties retrievalProperties) {
        this.retrievalProperties = retrievalProperties;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("enabled", retrievalProperties.enabled())
                .withDetail("embeddingDimension", retrievalProperties.embeddingDimension())
                .build();
    }
}

package com.willmear.sprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SprintReviewProperties.class, SprintReviewAiProperties.class})
public class SprintReviewConfig {
    // Spring Boot property binding only.
}

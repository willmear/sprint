package com.willmear.sprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SprintReviewProperties.class)
public class SprintReviewConfig {
    // TODO: Add generation strategy selection once AI-backed review generation is introduced.
}

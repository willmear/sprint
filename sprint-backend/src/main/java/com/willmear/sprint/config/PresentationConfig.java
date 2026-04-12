package com.willmear.sprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PresentationAiProperties.class)
public class PresentationConfig {
    // Spring Boot property binding only.
}

package com.willmear.sprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JobsProperties.class)
public class JobsConfig {
    // TODO: Add worker concurrency and retry policy configuration as the jobs framework evolves.
}

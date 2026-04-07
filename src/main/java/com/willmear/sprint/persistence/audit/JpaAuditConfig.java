package com.willmear.sprint.persistence.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
    // TODO: Add created-by and last-modified-by support when authentication is wired into persistence.
}


package com.willmear.sprint.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        // TODO: Configure module-specific serializers, naming, and JSON defaults.
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }
}

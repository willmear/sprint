package com.willmear.sprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(UiProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final UiProperties uiProperties;

    public WebConfig(UiProperties uiProperties) {
        this.uiProperties = uiProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(uiProperties.allowedOrigin())
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("X-Correlation-Id", "Content-Disposition");
    }
}

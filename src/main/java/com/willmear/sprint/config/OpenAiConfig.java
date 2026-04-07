package com.willmear.sprint.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiConfig {

    @Bean
    public RestClient openAiRestClient(OpenAiProperties openAiProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Duration timeout = openAiProperties.timeout() != null ? openAiProperties.timeout() : Duration.ofSeconds(30);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(normalizeBaseUrl(openAiProperties.baseUrl()))
                .requestFactory(requestFactory)
                .defaultHeader("Content-Type", "application/json");

        if (StringUtils.hasText(openAiProperties.apiKey())) {
            builder.defaultHeader("Authorization", "Bearer " + openAiProperties.apiKey());
        }

        return builder.build();
    }

    private String normalizeBaseUrl(String baseUrl) {
        return StringUtils.hasText(baseUrl) ? baseUrl : "https://api.openai.com";
    }
}

package com.willmear.sprint.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAiMessageDto(
        @JsonProperty("role")
        String role,
        @JsonProperty("content")
        String content
) {
    public static OpenAiMessageDto system(String content) {
        return new OpenAiMessageDto("system", content);
    }

    public static OpenAiMessageDto user(String content) {
        return new OpenAiMessageDto("user", content);
    }
}

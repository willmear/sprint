package com.willmear.sprint.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAiRequestMessageDto(
        @JsonProperty("role")
        String role,
        @JsonProperty("content")
        String content
) {
    public static OpenAiRequestMessageDto withRole(String role, String content) {
        return new OpenAiRequestMessageDto(role, content);
    }

    public static OpenAiRequestMessageDto user(String content) {
        return withRole("user", content);
    }
}

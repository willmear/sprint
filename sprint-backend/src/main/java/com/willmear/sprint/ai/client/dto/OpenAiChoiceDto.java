package com.willmear.sprint.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAiChoiceDto(
        @JsonProperty("index")
        Integer index,
        @JsonProperty("message")
        OpenAiMessageDto message,
        @JsonProperty("finish_reason")
        String finishReason
) {
}

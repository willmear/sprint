package com.willmear.sprint.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAiChatRequest(
        @JsonProperty("model")
        String model,
        @JsonProperty("messages")
        List<OpenAiMessageDto> messages,
        @JsonProperty("temperature")
        Double temperature,
        @JsonProperty("max_completion_tokens")
        Integer maxOutputTokens,
        @JsonProperty("response_format")
        OpenAiResponseFormatDto responseFormat
) {
}

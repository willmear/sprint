package com.willmear.sprint.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenAiChatResponse(
        @JsonProperty("id")
        String id,
        @JsonProperty("model")
        String model,
        @JsonProperty("choices")
        List<OpenAiChoiceDto> choices,
        @JsonProperty("usage")
        OpenAiUsageDto usage
) {
}

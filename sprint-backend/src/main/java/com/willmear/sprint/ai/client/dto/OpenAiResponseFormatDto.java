package com.willmear.sprint.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAiResponseFormatDto(
        @JsonProperty("type")
        String type
) {
}

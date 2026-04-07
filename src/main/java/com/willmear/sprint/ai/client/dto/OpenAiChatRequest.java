package com.willmear.sprint.ai.client.dto;

import java.util.List;

public record OpenAiChatRequest(
        String model,
        List<OpenAiMessageDto> messages,
        Double temperature,
        Integer maxOutputTokens
) {
    // TODO: Expand request fields as real structured output support is introduced.
}

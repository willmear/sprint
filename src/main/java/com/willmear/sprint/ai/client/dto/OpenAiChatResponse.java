package com.willmear.sprint.ai.client.dto;

import java.util.List;

public record OpenAiChatResponse(
        String model,
        List<OpenAiChoiceDto> choices,
        OpenAiUsageDto usage
) {
    // TODO: Expand transport coverage for refusals and tool calls if needed.
}

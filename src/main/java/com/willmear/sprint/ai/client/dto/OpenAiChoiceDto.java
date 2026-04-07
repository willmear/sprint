package com.willmear.sprint.ai.client.dto;

public record OpenAiChoiceDto(
        OpenAiMessageDto message,
        String finishReason
) {
}

package com.willmear.sprint.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record OpenAiMessageDto(
        @JsonProperty("role")
        String role,
        @JsonProperty("content")
        Object content
) {
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public String textContent() {
        if (content == null) {
            return "";
        }
        if (content instanceof String text) {
            return text;
        }
        if (content instanceof List<?> items) {
            StringBuilder builder = new StringBuilder();
            for (Object item : items) {
                String text = extractPartText(item);
                if (!text.isBlank()) {
                    if (builder.length() > 0) {
                        builder.append('\n');
                    }
                    builder.append(text);
                }
            }
            return builder.toString();
        }
        return extractPartText(content);
    }

    @SuppressWarnings("unchecked")
    private String extractPartText(Object node) {
        if (node == null) {
            return "";
        }
        if (node instanceof String text) {
            return text;
        }
        if (!(node instanceof Map<?, ?> map)) {
            return "";
        }
        Object directText = map.get("text");
        if (directText instanceof String text) {
            return text;
        }
        if (directText instanceof Map<?, ?> nestedTextMap) {
            Object nestedValue = nestedTextMap.get("value");
            if (nestedValue instanceof String textValue) {
                return textValue;
            }
        }
        Object outputText = map.get("output_text");
        if (outputText instanceof String text) {
            return text;
        }
        return "";
    }
}

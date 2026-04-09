package com.willmear.sprint.ai.parser;

import com.willmear.sprint.common.exception.AiResponseParseException;
import org.springframework.stereotype.Component;

@Component
public class AiJsonContentExtractor {

    public String extractJsonObject(String content) {
        return extract(content, '{', '}');
    }

    public String extractJsonArray(String content) {
        return extract(content, '[', ']');
    }

    private String extract(String content, char open, char close) {
        if (content == null || content.isBlank()) {
            throw new AiResponseParseException("AI response content was empty.", new IllegalArgumentException("empty_content"));
        }

        String sanitized = stripCodeFences(content).trim();
        if (!sanitized.isEmpty() && sanitized.charAt(0) == open) {
            return sanitized;
        }

        int start = sanitized.indexOf(open);
        int end = sanitized.lastIndexOf(close);
        if (start >= 0 && end > start) {
            return sanitized.substring(start, end + 1);
        }

        throw new AiResponseParseException("AI response did not contain the expected JSON payload.", new IllegalArgumentException("missing_json"));
    }

    private String stripCodeFences(String content) {
        String sanitized = content.trim();
        if (sanitized.startsWith("```")) {
            int firstNewline = sanitized.indexOf('\n');
            if (firstNewline > -1) {
                sanitized = sanitized.substring(firstNewline + 1);
            }
            if (sanitized.endsWith("```")) {
                sanitized = sanitized.substring(0, sanitized.length() - 3);
            }
        }
        return sanitized;
    }
}

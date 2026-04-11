package com.willmear.sprint.export.api.response;

import java.time.Instant;

public record ExportResponse(
        String format,
        String fileName,
        String contentType,
        String textContent,
        Object structuredContent,
        Instant generatedAt
) {
}

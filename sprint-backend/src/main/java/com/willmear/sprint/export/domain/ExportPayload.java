package com.willmear.sprint.export.domain;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

public record ExportPayload(
        ExportFormat format,
        String fileName,
        String contentType,
        String textContent,
        JsonNode structuredContent,
        Instant generatedAt
) {
}

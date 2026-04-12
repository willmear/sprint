package com.willmear.sprint.export.domain;

import java.time.Instant;

public record BinaryExportPayload(
        ExportFormat format,
        String fileName,
        String contentType,
        byte[] binaryContent,
        Instant generatedAt
) {
}

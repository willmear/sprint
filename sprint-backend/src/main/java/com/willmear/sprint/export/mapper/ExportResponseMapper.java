package com.willmear.sprint.export.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.export.api.response.ExportResponse;
import com.willmear.sprint.export.domain.ExportPayload;
import org.springframework.stereotype.Component;

@Component
public class ExportResponseMapper {

    private final ObjectMapper objectMapper;

    public ExportResponseMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ExportResponse toResponse(ExportPayload exportPayload) {
        return new ExportResponse(
                exportPayload.format().name(),
                exportPayload.fileName(),
                exportPayload.contentType(),
                exportPayload.textContent(),
                exportPayload.structuredContent() != null ? objectMapper.convertValue(exportPayload.structuredContent(), Object.class) : null,
                exportPayload.generatedAt()
        );
    }
}

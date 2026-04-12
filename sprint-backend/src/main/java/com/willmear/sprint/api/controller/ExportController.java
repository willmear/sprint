package com.willmear.sprint.api.controller;

import com.willmear.sprint.export.api.ExportService;
import com.willmear.sprint.export.domain.BinaryExportPayload;
import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.mapper.ExportResponseMapper;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;
    private final ExportResponseMapper exportResponseMapper;

    public ExportController(ExportService exportService, ExportResponseMapper exportResponseMapper) {
        this.exportService = exportService;
        this.exportResponseMapper = exportResponseMapper;
    }

    @GetMapping("/workspaces/{workspaceId}/sprints/{sprintId}/export")
    public ResponseEntity<com.willmear.sprint.export.api.response.ExportResponse> exportLatestSprintReview(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId,
            @RequestParam ExportFormat format
    ) {
        return ResponseEntity.ok(exportResponseMapper.toResponse(
                exportService.exportLatestSprintReview(workspaceId, sprintId, format)
        ));
    }

    @GetMapping("/artifacts/{artifactId}/export")
    public ResponseEntity<com.willmear.sprint.export.api.response.ExportResponse> exportArtifact(
            @PathVariable UUID artifactId,
            @RequestParam ExportFormat format
    ) {
        return ResponseEntity.ok(exportResponseMapper.toResponse(exportService.exportArtifact(artifactId, format)));
    }

    @GetMapping("/workspaces/{workspaceId}/slides/decks/{deckId}/export/powerpoint")
    public ResponseEntity<byte[]> exportDeckAsPowerPoint(
            @PathVariable UUID workspaceId,
            @PathVariable UUID deckId
    ) {
        return toBinaryResponse(exportService.exportPresentationDeckAsPowerPoint(workspaceId, deckId));
    }

    @GetMapping("/workspaces/{workspaceId}/sprints/{sprintId}/export/powerpoint")
    public ResponseEntity<byte[]> exportLatestDeckForSprintAsPowerPoint(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId
    ) {
        return toBinaryResponse(exportService.exportLatestDeckForSprintAsPowerPoint(workspaceId, sprintId));
    }

    private ResponseEntity<byte[]> toBinaryResponse(BinaryExportPayload payload) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(payload.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(payload.fileName()).build().toString()
                )
                .body(payload.binaryContent());
    }
}

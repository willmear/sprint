package com.willmear.sprint.api.controller;

import com.willmear.sprint.config.RetrievalProperties;
import com.willmear.sprint.retrieval.api.RetrievalService;
import com.willmear.sprint.retrieval.api.request.IndexSprintDocumentsRequest;
import com.willmear.sprint.retrieval.api.request.RetrievalSearchRequest;
import com.willmear.sprint.retrieval.api.response.EmbeddingDocumentSummaryResponse;
import com.willmear.sprint.retrieval.api.response.IndexingResponse;
import com.willmear.sprint.retrieval.api.response.RetrievalSearchResponse;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.mapper.RetrievalResultMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RetrievalController {

    private final RetrievalService retrievalService;
    private final RetrievalResultMapper retrievalResultMapper;
    private final RetrievalProperties retrievalProperties;

    public RetrievalController(
            RetrievalService retrievalService,
            RetrievalResultMapper retrievalResultMapper,
            RetrievalProperties retrievalProperties
    ) {
        this.retrievalService = retrievalService;
        this.retrievalResultMapper = retrievalResultMapper;
        this.retrievalProperties = retrievalProperties;
    }

    @PostMapping("/api/workspaces/{workspaceId}/sprints/{sprintId}/retrieval/index")
    public ResponseEntity<IndexingResponse> indexSprintDocuments(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId,
            @RequestBody(required = false) IndexSprintDocumentsRequest request
    ) {
        IndexSprintDocumentsRequest resolved = request != null
                ? request
                : new IndexSprintDocumentsRequest(
                        retrievalProperties.indexComments(),
                        retrievalProperties.indexSprintSummary(),
                        true
                );
        return ResponseEntity.ok(retrievalResultMapper.toResponse(
                retrievalService.indexSprintDocuments(
                        workspaceId,
                        sprintId,
                        resolved.includeComments() != null ? resolved.includeComments() : retrievalProperties.indexComments(),
                        resolved.includeSprintSummary() != null ? resolved.includeSprintSummary() : retrievalProperties.indexSprintSummary(),
                        resolved.forceReindex() != null ? resolved.forceReindex() : true
                )
        ));
    }

    @PostMapping("/api/workspaces/{workspaceId}/retrieval/search")
    public ResponseEntity<RetrievalSearchResponse> search(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody RetrievalSearchRequest request
    ) {
        return ResponseEntity.ok(retrievalResultMapper.toResponse(retrievalService.search(new RetrievalQuery(
                workspaceId,
                request.queryText(),
                request.topK(),
                request.externalSprintId(),
                request.sourceType(),
                null,
                request.includeContent() == null || request.includeContent(),
                true
        ))));
    }

    @GetMapping("/api/workspaces/{workspaceId}/sprints/{sprintId}/retrieval/documents")
    public ResponseEntity<List<EmbeddingDocumentSummaryResponse>> listDocuments(
            @PathVariable UUID workspaceId,
            @PathVariable Long sprintId
    ) {
        return ResponseEntity.ok(
                retrievalService.listSprintDocuments(workspaceId, sprintId).stream()
                        .map(retrievalResultMapper::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/api/workspaces/{workspaceId}/sprints/{sprintId}/retrieval/index")
    public ResponseEntity<Void> deleteIndex(@PathVariable UUID workspaceId, @PathVariable Long sprintId) {
        retrievalService.deleteSprintDocuments(workspaceId, sprintId);
        return ResponseEntity.noContent().build();
    }
}

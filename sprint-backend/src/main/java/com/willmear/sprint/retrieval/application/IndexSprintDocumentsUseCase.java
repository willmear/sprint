package com.willmear.sprint.retrieval.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.willmear.sprint.common.exception.RetrievalIndexingException;
import com.willmear.sprint.observability.logging.LoggingContextHelper;
import com.willmear.sprint.observability.logging.MdcKeys;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.observability.tracing.TraceNames;
import com.willmear.sprint.retrieval.application.support.SprintRetrievalSourceBundle;
import com.willmear.sprint.retrieval.domain.model.DocumentChunk;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocument;
import com.willmear.sprint.retrieval.domain.model.IndexingResult;
import com.willmear.sprint.retrieval.domain.model.SourceType;
import com.willmear.sprint.retrieval.domain.port.EmbeddingGeneratorPort;
import com.willmear.sprint.retrieval.domain.port.EmbeddingStorePort;
import com.willmear.sprint.retrieval.domain.port.SprintRetrievalSourcePort;
import com.willmear.sprint.retrieval.domain.service.ChunkingService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IndexSprintDocumentsUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexSprintDocumentsUseCase.class);

    private final SprintRetrievalSourcePort sprintRetrievalSourcePort;
    private final ChunkingService chunkingService;
    private final EmbeddingGeneratorPort embeddingGeneratorPort;
    private final EmbeddingStorePort embeddingStorePort;
    private final ObjectMapper objectMapper;
    private final LoggingContextHelper loggingContextHelper;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;
    private final TraceContextHelper traceContextHelper;

    public IndexSprintDocumentsUseCase(
            SprintRetrievalSourcePort sprintRetrievalSourcePort,
            ChunkingService chunkingService,
            EmbeddingGeneratorPort embeddingGeneratorPort,
            EmbeddingStorePort embeddingStorePort,
            ObjectMapper objectMapper,
            LoggingContextHelper loggingContextHelper,
            WorkflowMetricsRecorder workflowMetricsRecorder,
            TraceContextHelper traceContextHelper
    ) {
        this.sprintRetrievalSourcePort = sprintRetrievalSourcePort;
        this.chunkingService = chunkingService;
        this.embeddingGeneratorPort = embeddingGeneratorPort;
        this.embeddingStorePort = embeddingStorePort;
        this.objectMapper = objectMapper;
        this.loggingContextHelper = loggingContextHelper;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
        this.traceContextHelper = traceContextHelper;
    }

    public IndexingResult index(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeSprintSummary, boolean forceReindex) {
        long startedAt = System.nanoTime();
        try (LoggingContextHelper.Scope ignored = loggingContextHelper.putAll(Map.of(
                MdcKeys.WORKSPACE_ID, workspaceId,
                MdcKeys.SPRINT_ID, externalSprintId
        )); TraceContextHelper.Scope trace = traceContextHelper.start(TraceNames.RETRIEVAL_INDEX)) {
            workflowMetricsRecorder.increment("retrieval.index.started");
            LOGGER.info("retrieval.index.start workspaceId={} sprintId={} includeComments={} includeSprintSummary={} forceReindex={}",
                    workspaceId, externalSprintId, includeComments, includeSprintSummary, forceReindex);
            SprintRetrievalSourceBundle bundle = sprintRetrievalSourcePort.load(workspaceId, externalSprintId, includeComments, includeSprintSummary);
            List<DocumentChunk> chunks = buildChunks(bundle, includeComments, includeSprintSummary);
            List<List<Double>> embeddings = embeddingGeneratorPort.generateEmbeddings(chunks.stream().map(DocumentChunk::text).toList());
            Instant now = Instant.now();
            List<EmbeddingDocument> documents = new ArrayList<>();
            for (int index = 0; index < chunks.size(); index++) {
                DocumentChunk chunk = chunks.get(index);
                documents.add(new EmbeddingDocument(
                        UUID.randomUUID(),
                        workspaceId,
                        bundle.sprint().getJiraConnection().getId(),
                        externalSprintId,
                        chunk.sourceType(),
                        chunk.sourceId(),
                        chunk.sourceKey(),
                        chunk.title(),
                        chunk.content(),
                        chunk.text(),
                        chunk.chunkIndex(),
                        chunk.tokenCountEstimate(),
                        chunk.metadata(),
                        embeddings.size() > index ? embeddings.get(index) : List.of(),
                        now,
                        now,
                        now
                ));
            }
            int deleted = embeddingStorePort.replaceSprintDocuments(workspaceId, externalSprintId, documents);
            workflowMetricsRecorder.increment("retrieval.index.completed");
            workflowMetricsRecorder.recordDuration("retrieval.index.duration", System.nanoTime() - startedAt, "status", "completed");
            workflowMetricsRecorder.recordCount("retrieval.index.document.count", documents.size());
            LOGGER.info("retrieval.index.completed workspaceId={} sprintId={} indexedDocuments={} deletedDocuments={}",
                    workspaceId, externalSprintId, documents.size(), deleted);
            trace.close("completed");
            return new IndexingResult(documents.size(), deleted, now, forceReindex
                    ? "Sprint documents reindexed."
                    : "Sprint documents indexed.");
        } catch (RuntimeException exception) {
            workflowMetricsRecorder.increment("retrieval.index.failed");
            workflowMetricsRecorder.recordDuration("retrieval.index.duration", System.nanoTime() - startedAt, "status", "failed");
            LOGGER.error("retrieval.index.failed workspaceId={} sprintId={}", workspaceId, externalSprintId, exception);
            throw exception instanceof RetrievalIndexingException ? (RetrievalIndexingException) exception
                    : new RetrievalIndexingException("Failed to index sprint documents.", exception);
        }
    }

    private List<DocumentChunk> buildChunks(SprintRetrievalSourceBundle bundle, boolean includeComments, boolean includeSprintSummary) {
        List<DocumentChunk> chunks = new ArrayList<>();

        if (includeSprintSummary) {
            ObjectNode metadata = objectMapper.createObjectNode();
            metadata.put("sourceType", SourceType.SPRINT.name());
            metadata.put("sprintId", bundle.sprint().getExternalSprintId());
            metadata.put("state", bundle.sprint().getState());
            chunks.addAll(chunkingService.chunk(chunkingService.createSeed(
                    SourceType.SPRINT.name(),
                    bundle.sprint().getExternalSprintId().toString(),
                    bundle.sprint().getName(),
                    bundle.sprint().getName(),
                    "Sprint " + bundle.sprint().getName() + "\nGoal: " + nullSafe(bundle.sprint().getGoal()) + "\nState: " + bundle.sprint().getState(),
                    metadata
            )));
        }

        bundle.issues().forEach(issue -> {
            ObjectNode metadata = objectMapper.createObjectNode();
            metadata.put("sourceType", SourceType.ISSUE.name());
            metadata.put("issueKey", issue.getIssueKey());
            metadata.put("issueType", nullSafe(issue.getIssueType()));
            metadata.put("status", nullSafe(issue.getStatus()));
            metadata.put("assignee", nullSafe(issue.getAssigneeDisplayName()));
            metadata.put("sprintId", issue.getExternalSprintId());
            String issueBody = issue.getIssueKey() + "\nSummary: " + issue.getSummary() + "\nDescription: " + nullSafe(issue.getDescription());
            chunks.addAll(chunkingService.chunk(chunkingService.createSeed(
                    SourceType.ISSUE.name(),
                    issue.getExternalIssueId(),
                    issue.getIssueKey(),
                    issue.getSummary(),
                    issueBody,
                    metadata
            )));

            if (includeComments) {
                bundle.commentsByIssueKey().getOrDefault(issue.getIssueKey(), List.of()).forEach(comment -> {
                    ObjectNode commentMetadata = objectMapper.createObjectNode();
                    commentMetadata.put("sourceType", SourceType.COMMENT.name());
                    commentMetadata.put("issueKey", issue.getIssueKey());
                    commentMetadata.put("commentId", comment.getExternalCommentId());
                    commentMetadata.put("sprintId", issue.getExternalSprintId());
                    String commentBody = issue.getIssueKey() + " comment by " + nullSafe(comment.getAuthorDisplayName())
                            + "\n" + nullSafe(comment.getBody());
                    chunks.addAll(chunkingService.chunk(chunkingService.createSeed(
                            SourceType.COMMENT.name(),
                            comment.getExternalCommentId(),
                            issue.getIssueKey(),
                            issue.getSummary(),
                            commentBody,
                            commentMetadata
                    )));
                });
            }
        });

        return chunks;
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}

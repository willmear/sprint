package com.willmear.sprint.retrieval.application;

import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import com.willmear.sprint.retrieval.domain.port.EmbeddingGeneratorPort;
import com.willmear.sprint.retrieval.domain.port.EmbeddingStorePort;
import com.willmear.sprint.retrieval.domain.service.MetadataFilterService;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.observability.tracing.TraceNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchSprintContextUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchSprintContextUseCase.class);

    private final EmbeddingStorePort embeddingStorePort;
    private final EmbeddingGeneratorPort embeddingGeneratorPort;
    private final MetadataFilterService metadataFilterService;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;
    private final TraceContextHelper traceContextHelper;

    public SearchSprintContextUseCase(
            EmbeddingStorePort embeddingStorePort,
            EmbeddingGeneratorPort embeddingGeneratorPort,
            MetadataFilterService metadataFilterService,
            WorkflowMetricsRecorder workflowMetricsRecorder,
            TraceContextHelper traceContextHelper
    ) {
        this.embeddingStorePort = embeddingStorePort;
        this.embeddingGeneratorPort = embeddingGeneratorPort;
        this.metadataFilterService = metadataFilterService;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
        this.traceContextHelper = traceContextHelper;
    }

    public RetrievalResultSet search(RetrievalQuery query) {
        long startedAt = System.nanoTime();
        try (TraceContextHelper.Scope trace = traceContextHelper.start(TraceNames.RETRIEVAL_SEARCH)) {
            RetrievalQuery normalized = metadataFilterService.normalize(query);
            workflowMetricsRecorder.increment("retrieval.search.requests");
            RetrievalResultSet resultSet = embeddingStorePort.search(normalized, embeddingGeneratorPort.generateEmbedding(normalized.queryText()));
            workflowMetricsRecorder.recordDuration("retrieval.search.duration", System.nanoTime() - startedAt, "status", "completed");
            workflowMetricsRecorder.recordCount("retrieval.search.result.count", resultSet.totalReturned());
            LOGGER.info("retrieval.search.completed workspaceId={} sprintId={} sourceType={} topK={} results={}",
                    normalized.workspaceId(), normalized.externalSprintId(), normalized.sourceType(), normalized.topK(), resultSet.totalReturned());
            trace.close("completed");
            return resultSet;
        } catch (RuntimeException exception) {
            workflowMetricsRecorder.recordDuration("retrieval.search.duration", System.nanoTime() - startedAt, "status", "failed");
            LOGGER.error("retrieval.search.failed workspaceId={} sprintId={} sourceType={}",
                    query.workspaceId(), query.externalSprintId(), query.sourceType(), exception);
            throw exception;
        }
    }
}

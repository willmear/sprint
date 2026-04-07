package com.willmear.sprint.retrieval.application;

import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResult;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import com.willmear.sprint.retrieval.domain.port.EmbeddingGeneratorPort;
import com.willmear.sprint.retrieval.domain.port.EmbeddingStorePort;
import com.willmear.sprint.retrieval.domain.service.MetadataFilterService;
import com.willmear.sprint.config.RetrievalProperties;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchSprintContextUseCaseTest {

    private final EmbeddingStorePort embeddingStorePort = mock(EmbeddingStorePort.class);
    private final EmbeddingGeneratorPort embeddingGeneratorPort = mock(EmbeddingGeneratorPort.class);
    private final SearchSprintContextUseCase useCase = new SearchSprintContextUseCase(
            embeddingStorePort,
            embeddingGeneratorPort,
            new MetadataFilterService(new RetrievalProperties(true, true, true, 5, 1200, 150, 1536, true)),
            new WorkflowMetricsRecorder(new SimpleMeterRegistry()),
            new TraceContextHelper()
    );

    @Test
    void shouldNormalizeQueryAndReturnResults() {
        RetrievalQuery query = new RetrievalQuery(UUID.randomUUID(), "accomplishments", null, 44L, "ISSUE", null, true, true);
        RetrievalResultSet resultSet = new RetrievalResultSet(
                List.of(new RetrievalResult(UUID.randomUUID(), "ISSUE", "100", "SPR-1", "Title", "Snippet", 0.91, null)),
                1,
                Instant.now()
        );
        when(embeddingGeneratorPort.generateEmbedding("accomplishments")).thenReturn(List.of(0.1, 0.2));
        when(embeddingStorePort.search(org.mockito.ArgumentMatchers.any(RetrievalQuery.class), org.mockito.ArgumentMatchers.eq(List.of(0.1, 0.2))))
                .thenReturn(resultSet);

        RetrievalResultSet returned = useCase.search(query);

        assertThat(returned.totalReturned()).isEqualTo(1);
        verify(embeddingGeneratorPort).generateEmbedding("accomplishments");
        verify(embeddingStorePort).search(org.mockito.ArgumentMatchers.argThat(normalized -> normalized.topK() == 5), org.mockito.ArgumentMatchers.eq(List.of(0.1, 0.2)));
    }
}

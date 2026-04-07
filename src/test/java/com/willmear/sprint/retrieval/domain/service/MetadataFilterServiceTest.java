package com.willmear.sprint.retrieval.domain.service;

import com.willmear.sprint.config.RetrievalProperties;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetadataFilterServiceTest {

    private final MetadataFilterService service = new MetadataFilterService(new RetrievalProperties(true, true, true, 7, 1200, 150, 1536, true));

    @Test
    void shouldApplyDefaultTopKWhenMissing() {
        RetrievalQuery normalized = service.normalize(new RetrievalQuery(UUID.randomUUID(), "query", null, 22L, null, null, true, true));

        assertThat(normalized.topK()).isEqualTo(7);
    }

    @Test
    void shouldApplyDefaultTopKWhenInvalid() {
        RetrievalQuery normalized = service.normalize(new RetrievalQuery(UUID.randomUUID(), "query", 0, 22L, "ISSUE", null, true, false));

        assertThat(normalized.topK()).isEqualTo(7);
        assertThat(normalized.sourceType()).isEqualTo("ISSUE");
    }

    @Test
    void shouldPreserveValidTopK() {
        RetrievalQuery normalized = service.normalize(new RetrievalQuery(UUID.randomUUID(), "query", 3, null, null, null, false, false));

        assertThat(normalized.topK()).isEqualTo(3);
        assertThat(normalized.includeContent()).isFalse();
        assertThat(normalized.includeScores()).isFalse();
    }
}

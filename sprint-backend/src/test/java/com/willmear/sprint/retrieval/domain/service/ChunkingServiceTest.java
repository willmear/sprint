package com.willmear.sprint.retrieval.domain.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.willmear.sprint.config.RetrievalProperties;
import com.willmear.sprint.retrieval.domain.model.DocumentChunk;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChunkingServiceTest {

    private final ChunkingService service = new ChunkingService(new RetrievalProperties(true, true, true, 5, 120, 20, 1536, true));

    @Test
    void shouldReturnNoChunksForBlankText() {
        List<DocumentChunk> chunks = service.chunk(new DocumentChunk("ISSUE", "1", "SPR-1", "Title", "", "   ", 0, 0, JsonNodeFactory.instance.objectNode()));

        assertThat(chunks).isEmpty();
    }

    @Test
    void shouldCreateChunkedDocumentsWithIndexesAndTokenEstimates() {
        String text = "A".repeat(450);

        List<DocumentChunk> chunks = service.chunk(service.createSeed(
                "ISSUE",
                "1",
                "SPR-1",
                "Title",
                text,
                JsonNodeFactory.instance.objectNode()
        ));

        assertThat(chunks).hasSize(3);
        assertThat(chunks).extracting(DocumentChunk::chunkIndex).containsExactly(0, 1, 2);
        assertThat(chunks).allMatch(chunk -> chunk.tokenCountEstimate() > 0);
    }

    @Test
    void shouldCreateSeedWithOriginalContentAndEstimatedTokens() {
        DocumentChunk chunk = service.createSeed("ISSUE", "1", "SPR-1", "Title", "Some content here", JsonNodeFactory.instance.objectNode());

        assertThat(chunk.content()).isEqualTo("Some content here");
        assertThat(chunk.text()).isEqualTo("Some content here");
        assertThat(chunk.tokenCountEstimate()).isPositive();
    }
}

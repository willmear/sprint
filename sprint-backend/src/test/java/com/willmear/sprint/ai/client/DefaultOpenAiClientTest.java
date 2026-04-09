package com.willmear.sprint.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;
import com.willmear.sprint.config.OpenAiProperties;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefaultOpenAiClientTest {

    private final DefaultOpenAiClient client = new DefaultOpenAiClient(
            mock(RestClient.class),
            new OpenAiProperties(true, true, "", "https://api.openai.com", "/v1/chat/completions", "/v1/embeddings",
                    "gpt-test", "text-embedding-test", Duration.ofSeconds(5), 500, 0.2),
            new ObjectMapper()
    );

    @Test
    void shouldReturnMockChatResponseForKnownPrompt() {
        AiResponse response = client.generate(new AiGenerationRequest(
                "sprintreview",
                "sprint-review",
                "gpt-test",
                new AiPrompt("sprint-review", "v2", "system", "user", "json", Map.of()),
                0.2,
                500,
                true,
                "schema"
        ));

        assertThat(response.success()).isTrue();
        assertThat(response.content()).contains("Sprint Review: Mock Sprint");
        assertThat(response.tokenUsage().totalTokens()).isEqualTo(200);
    }

    @Test
    void shouldReturnMockEmbeddings() {
        EmbeddingResponse response = client.embed(new EmbeddingRequest("text-embedding-test", List.of("hello")));

        assertThat(response.embeddings()).hasSize(1);
        assertThat(response.embeddings().getFirst()).containsExactly(0.01, 0.02, 0.03);
    }
}

package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.client.OpenAiClient;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;
import com.willmear.sprint.ai.domain.model.TokenUsage;
import com.willmear.sprint.ai.domain.service.AiResponseValidator;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultAiGenerationServiceTest {

    private final OpenAiClient openAiClient = mock(OpenAiClient.class);
    private final AiResponseValidator aiResponseValidator = mock(AiResponseValidator.class);
    private final WorkflowMetricsRecorder metricsRecorder = new WorkflowMetricsRecorder(new SimpleMeterRegistry());
    private final DefaultAiGenerationService service = new DefaultAiGenerationService(
            openAiClient,
            aiResponseValidator,
            metricsRecorder,
            new TraceContextHelper()
    );

    @Test
    void shouldGenerateAndParseStructuredResult() {
        AiGenerationRequest request = request();
        AiResponse response = new AiResponse("{\"message\":\"ok\"}", "gpt-test", new TokenUsage(10, 5, 15), Instant.now(), "stop", true, null);
        when(openAiClient.generate(request)).thenReturn(response);
        when(aiResponseValidator.validate(response)).thenReturn(response);

        AiGenerationResult<String> result = service.generate(request, raw -> raw.content());

        assertThat(result.parsedResult()).isEqualTo("{\"message\":\"ok\"}");
        assertThat(result.rawResponse()).isEqualTo(response);
        assertThat(result.aiRun().workflowName()).isEqualTo("workflow");
    }

    @Test
    void shouldEmbedUsingOpenAiClient() {
        EmbeddingRequest request = new EmbeddingRequest("embed-model", List.of("text"));
        EmbeddingResponse response = new EmbeddingResponse(List.of(List.of(0.1, 0.2)), "embed-model", new TokenUsage(1, 0, 1));
        when(openAiClient.embed(request)).thenReturn(response);

        EmbeddingResponse embedded = service.embed(request);

        assertThat(embedded).isEqualTo(response);
    }

    @Test
    void shouldPropagateGenerationFailures() {
        AiGenerationRequest request = request();
        when(openAiClient.generate(request)).thenThrow(new IllegalStateException("boom"));

        assertThatThrownBy(() -> service.generateRaw(request))
                .isInstanceOf(IllegalStateException.class);
    }

    private AiGenerationRequest request() {
        return new AiGenerationRequest(
                "workflow",
                "prompt",
                "gpt-test",
                new AiPrompt("prompt", "v1", "system", "user", "json", Map.of()),
                0.2,
                400,
                true,
                "schema"
        );
    }
}

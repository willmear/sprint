package com.willmear.sprint.ai.application;

import com.willmear.sprint.ai.api.AiGenerationService;
import com.willmear.sprint.ai.client.OpenAiClient;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiGenerationResult;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.AiRun;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;
import com.willmear.sprint.ai.domain.service.AiResponseValidator;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.observability.tracing.TraceNames;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefaultAiGenerationService implements AiGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAiGenerationService.class);

    private final OpenAiClient openAiClient;
    private final AiResponseValidator aiResponseValidator;
    private final WorkflowMetricsRecorder workflowMetricsRecorder;
    private final TraceContextHelper traceContextHelper;

    public DefaultAiGenerationService(
            OpenAiClient openAiClient,
            AiResponseValidator aiResponseValidator,
            WorkflowMetricsRecorder workflowMetricsRecorder,
            TraceContextHelper traceContextHelper
    ) {
        this.openAiClient = openAiClient;
        this.aiResponseValidator = aiResponseValidator;
        this.workflowMetricsRecorder = workflowMetricsRecorder;
        this.traceContextHelper = traceContextHelper;
    }

    @Override
    public AiResponse generateRaw(AiGenerationRequest request) {
        long startedAt = System.nanoTime();
        try (TraceContextHelper.Scope trace = traceContextHelper.start(TraceNames.AI_GENERATION)) {
            LOGGER.info("ai.request.start workflow={} prompt={} model={}", request.workflowName(), request.promptName(), request.model());
            AiResponse response = aiResponseValidator.validate(openAiClient.generate(request));
            workflowMetricsRecorder.increment("ai.requests", "workflow", request.workflowName(), "prompt", request.promptName(), "mode", "chat");
            workflowMetricsRecorder.recordDuration("ai.request.duration", System.nanoTime() - startedAt, "workflow", request.workflowName(), "prompt", request.promptName(), "mode", "chat");
            if (response.tokenUsage() != null) {
                workflowMetricsRecorder.recordCount("ai.tokens.input", response.tokenUsage().inputTokens(), "workflow", request.workflowName());
                workflowMetricsRecorder.recordCount("ai.tokens.output", response.tokenUsage().outputTokens(), "workflow", request.workflowName());
            }
            LOGGER.info(
                    "ai.request.completed workflow={} prompt={} model={} finishReason={} contentLength={} contentPreview={}",
                    request.workflowName(),
                    request.promptName(),
                    response.model(),
                    response.finishReason(),
                    response.content() != null ? response.content().length() : 0,
                    preview(response.content())
            );
            trace.close("completed");
            return response;
        } catch (RuntimeException exception) {
            workflowMetricsRecorder.increment("ai.failures", "workflow", request.workflowName(), "prompt", request.promptName(), "mode", "chat");
            workflowMetricsRecorder.recordDuration("ai.request.duration", System.nanoTime() - startedAt, "workflow", request.workflowName(), "prompt", request.promptName(), "mode", "chat");
            LOGGER.error("ai.request.failed workflow={} prompt={} model={}", request.workflowName(), request.promptName(), request.model(), exception);
            throw exception;
        }
    }

    private String preview(String content) {
        if (content == null || content.isBlank()) {
            return "<empty>";
        }
        String sanitized = content.replace('\n', ' ').replace('\r', ' ').trim();
        return sanitized.length() <= 240 ? sanitized : sanitized.substring(0, 240) + "...";
    }

    @Override
    public <T> AiGenerationResult<T> generate(AiGenerationRequest request, Function<AiResponse, T> parser) {
        Instant startedAt = Instant.now();
        AiResponse response = generateRaw(request);
        T parsedResult = parser.apply(response);
        AiRun aiRun = new AiRun(
                UUID.randomUUID(),
                request.workflowName(),
                request.promptName(),
                request.prompt().version(),
                response.model(),
                startedAt,
                Instant.now(),
                response.tokenUsage(),
                response.success(),
                response.success() ? null : response.refusalReason()
        );
        return new AiGenerationResult<>(parsedResult, response, aiRun);
    }

    @Override
    public EmbeddingResponse embed(EmbeddingRequest request) {
        long startedAt = System.nanoTime();
        try {
            EmbeddingResponse response = openAiClient.embed(request);
            workflowMetricsRecorder.increment("ai.requests", "workflow", "embedding", "prompt", "embedding", "mode", "embedding");
            workflowMetricsRecorder.recordDuration("ai.request.duration", System.nanoTime() - startedAt, "workflow", "embedding", "prompt", "embedding", "mode", "embedding");
            return response;
        } catch (RuntimeException exception) {
            workflowMetricsRecorder.increment("ai.failures", "workflow", "embedding", "prompt", "embedding", "mode", "embedding");
            workflowMetricsRecorder.recordDuration("ai.request.duration", System.nanoTime() - startedAt, "workflow", "embedding", "prompt", "embedding", "mode", "embedding");
            LOGGER.error("ai.embedding.failed model={}", request.model(), exception);
            throw exception;
        }
    }
}

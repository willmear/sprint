package com.willmear.sprint.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.client.dto.OpenAiChatRequest;
import com.willmear.sprint.ai.client.dto.OpenAiChatResponse;
import com.willmear.sprint.ai.client.dto.OpenAiChoiceDto;
import com.willmear.sprint.ai.client.dto.OpenAiEmbeddingRequest;
import com.willmear.sprint.ai.client.dto.OpenAiEmbeddingResponse;
import com.willmear.sprint.ai.client.dto.OpenAiMessageDto;
import com.willmear.sprint.ai.client.dto.OpenAiUsageDto;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;
import com.willmear.sprint.ai.domain.model.TokenUsage;
import com.willmear.sprint.common.exception.AiClientException;
import com.willmear.sprint.config.OpenAiProperties;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Component;

@Component
public class DefaultOpenAiClient implements OpenAiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOpenAiClient.class);

    private final RestClient openAiRestClient;
    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;

    public DefaultOpenAiClient(RestClient openAiRestClient, OpenAiProperties openAiProperties, ObjectMapper objectMapper) {
        this.openAiRestClient = openAiRestClient;
        this.openAiProperties = openAiProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiResponse generate(AiGenerationRequest request) {
        if (!openAiProperties.enabled() || openAiProperties.mockMode()) {
            LOGGER.info("openai.chat.mock workflow={} prompt={} model={}", request.workflowName(), request.promptName(), request.model());
            return mockResponse(request);
        }

        try {
            OpenAiChatRequest chatRequest = new OpenAiChatRequest(
                    request.model() != null ? request.model() : openAiProperties.model(),
                    List.of(
                            new OpenAiMessageDto("system", request.prompt().systemPrompt()),
                            new OpenAiMessageDto("user", request.prompt().userPrompt())
                    ),
                    request.temperature(),
                    request.maxOutputTokens()
            );
            OpenAiChatResponse response = openAiRestClient.post()
                    .uri(openAiProperties.chatPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(chatRequest)
                    .retrieve()
                    .body(OpenAiChatResponse.class);

            String content = response != null && response.choices() != null && !response.choices().isEmpty()
                    ? response.choices().getFirst().message().content()
                    : "";
            TokenUsage tokenUsage = mapUsage(response != null ? response.usage() : null);
            return new AiResponse(
                    content,
                    response != null ? response.model() : chatRequest.model(),
                    tokenUsage,
                    Instant.now(),
                    response != null && response.choices() != null && !response.choices().isEmpty()
                            ? response.choices().getFirst().finishReason()
                            : null,
                    true,
                    null
            );
        } catch (RuntimeException exception) {
            throw new AiClientException("OpenAI chat generation failed.", exception);
        }
    }

    @Override
    public EmbeddingResponse embed(EmbeddingRequest request) {
        if (!openAiProperties.enabled() || openAiProperties.mockMode()) {
            LOGGER.info("openai.embedding.mock model={}", request.model() != null ? request.model() : openAiProperties.embeddingModel());
            return new EmbeddingResponse(List.of(List.of(0.01, 0.02, 0.03)), openAiProperties.embeddingModel(), new TokenUsage(0, 0, 0));
        }
        try {
            OpenAiEmbeddingResponse response = openAiRestClient.post()
                    .uri(openAiProperties.embeddingsPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new OpenAiEmbeddingRequest(
                            request.model() != null ? request.model() : openAiProperties.embeddingModel(),
                            request.input()
                    ))
                    .retrieve()
                    .body(OpenAiEmbeddingResponse.class);
            return new EmbeddingResponse(
                    response != null ? response.embeddings() : List.of(),
                    response != null ? response.model() : openAiProperties.embeddingModel(),
                    mapUsage(response != null ? response.usage() : null)
            );
        } catch (RuntimeException exception) {
            throw new AiClientException("OpenAI embedding generation failed.", exception);
        }
    }

    private AiResponse mockResponse(AiGenerationRequest request) {
        String content = switch (request.promptName()) {
            case "sprint-summary" -> """
                    {"title":"Sprint Review: Mock Sprint","overview":"The sprint delivered several meaningful items with steady progress.","deliverySummary":"Delivery remained consistent across the sprint.","qualitySummary":"A few bug fixes improved reliability.","outcomeSummary":"Most planned work landed, with a small amount carried forward."}
                    """;
            case "sprint-themes" -> """
                    [{"name":"Feature Delivery","description":"The sprint centered on shipping customer-facing work.","relatedIssueKeys":["SPR-101","SPR-102"]},
                    {"name":"Stability Improvements","description":"Bug fixes and cleanup improved reliability.","relatedIssueKeys":["SPR-103"]}]
                    """;
            case "sprint-speaker-notes" -> """
                    [{"section":"Introduction","note":"Open with the sprint goal and delivery scope.","displayOrder":1},
                    {"section":"Highlights","note":"Cover the main completed items and their impact.","displayOrder":2},
                    {"section":"Risks","note":"Mention carried-over work and any blockers.","displayOrder":3}]
                    """;
            default -> "{\"message\":\"Mock response not configured for prompt\"}";
        };

        return new AiResponse(
                content,
                request.model() != null ? request.model() : openAiProperties.model(),
                new TokenUsage(120, 80, 200),
                Instant.now(),
                "stop",
                true,
                null
        );
    }

    private TokenUsage mapUsage(OpenAiUsageDto usage) {
        if (usage == null) {
            return new TokenUsage(0, 0, 0);
        }
        return new TokenUsage(usage.promptTokens(), usage.completionTokens(), usage.totalTokens());
    }
}

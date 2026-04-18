package com.willmear.sprint.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.client.dto.OpenAiChatRequest;
import com.willmear.sprint.ai.client.dto.OpenAiChatResponse;
import com.willmear.sprint.ai.client.dto.OpenAiChoiceDto;
import com.willmear.sprint.ai.client.dto.OpenAiMessageDto;
import com.willmear.sprint.ai.client.dto.OpenAiRequestMessageDto;
import com.willmear.sprint.ai.client.dto.OpenAiResponseFormatDto;
import com.willmear.sprint.ai.client.dto.OpenAiUsageDto;
import com.willmear.sprint.ai.domain.model.AiGenerationRequest;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.EmbeddingRequest;
import com.willmear.sprint.ai.domain.model.EmbeddingResponse;
import com.willmear.sprint.ai.domain.model.TokenUsage;
import com.willmear.sprint.common.exception.AiClientException;
import com.willmear.sprint.config.OpenAiProperties;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

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
            String model = request.model() != null ? request.model() : openAiProperties.model();
            OpenAiChatResponse response = openAiRestClient.post()
                    .uri(openAiProperties.chatPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildChatRequest(request, model))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (requestSpec, clientResponse) -> {
                        String errorBody;
                        try {
                            errorBody = StreamUtils.copyToString(clientResponse.getBody(), StandardCharsets.UTF_8);
                        } catch (Exception exception) {
                            errorBody = clientResponse.getStatusText();
                        }
                        throw new AiClientException(
                                "OpenAI chat generation failed with status " + clientResponse.getStatusCode() + ": " + errorBody,
                                new IllegalStateException(clientResponse.getStatusText())
                        );
                    })
                    .body(OpenAiChatResponse.class);

            String content = extractMessageContent(response);
            TokenUsage tokenUsage = mapUsage(response != null ? response.usage() : null);
            return new AiResponse(
                    content,
                    response != null && response.model() != null ? response.model() : model,
                    tokenUsage,
                    Instant.now(),
                    response != null && response.choices() != null && !response.choices().isEmpty()
                            ? response.choices().getFirst().finishReason()
                            : null,
                    true,
                    null
            );
        } catch (Exception exception) {
            throw new AiClientException("OpenAI chat generation failed.", exception);
        }
    }

    @Override
    public EmbeddingResponse embed(EmbeddingRequest request) {
        if (!openAiProperties.enabled() || openAiProperties.mockMode()) {
            LOGGER.info("openai.embedding.mock model={}", request.model() != null ? request.model() : openAiProperties.embeddingModel());
            return new EmbeddingResponse(List.of(List.of(0.01, 0.02, 0.03)), request.model() != null ? request.model() : openAiProperties.embeddingModel(), new TokenUsage(0, 0, 0));
        }
        try {
            String model = request.model() != null ? request.model() : openAiProperties.embeddingModel();
            String responseBody = openAiRestClient.post()
                    .uri(openAiProperties.embeddingsPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "model", model,
                            "input", request.input()
                    ))
                    .retrieve()
                    .body(String.class);

            JsonNode response = objectMapper.readTree(responseBody == null ? "{}" : responseBody);
            return new EmbeddingResponse(
                    extractEmbeddings(response),
                    response.path("model").asText(model),
                    mapUsage(objectMapper.treeToValue(response.path("usage"), OpenAiUsageDto.class))
            );
        } catch (Exception exception) {
            throw new AiClientException("OpenAI embedding generation failed.", exception);
        }
    }

    private AiResponse mockResponse(AiGenerationRequest request) {
        String content = switch (request.promptName()) {
            case "sprint-review" -> """
                    {
                      "summary": {
                        "title": "Sprint Review: Mock Sprint",
                        "overview": "The sprint delivered several meaningful items with steady progress and a small amount of unfinished work.",
                        "deliverySummary": "Delivery remained consistent across the sprint with clear progress on the planned scope.",
                        "qualitySummary": "The team also invested in stability and cleanup work to improve reliability.",
                        "outcomeSummary": "Most planned work landed, with a small dependency-related item carried forward."
                      },
                      "themes": [
                        {
                          "name": "Feature Delivery",
                          "description": "The sprint centered on shipping customer-facing work.",
                          "relatedIssueKeys": ["SPR-101", "SPR-102"]
                        },
                        {
                          "name": "Reliability Improvements",
                          "description": "Bug fixes and technical cleanup improved quality.",
                          "relatedIssueKeys": ["SPR-103"]
                        }
                      ],
                      "highlights": [
                        {
                          "title": "Delivered key customer-facing work",
                          "description": "The team shipped the highest-value stories planned for the sprint.",
                          "relatedIssueKeys": ["SPR-101", "SPR-102"],
                          "category": "FEATURE"
                        },
                        {
                          "title": "Improved platform reliability",
                          "description": "The sprint included targeted stability work and bug fixes.",
                          "relatedIssueKeys": ["SPR-103"],
                          "category": "BUGFIX"
                        }
                      ],
                      "blockers": [
                        {
                          "title": "Carried-over dependency work",
                          "description": "A small amount of dependency work rolled into the next sprint.",
                          "relatedIssueKeys": ["SPR-104"],
                          "severity": "MEDIUM"
                        }
                      ],
                      "speakerNotes": [
                        {
                          "section": "Introduction",
                          "note": "Open with the sprint goal and the overall delivery scope.",
                          "displayOrder": 1
                        },
                        {
                          "section": "Highlights",
                          "note": "Cover the main completed items and the impact they had.",
                          "displayOrder": 2
                        },
                        {
                          "section": "Risks",
                          "note": "Mention the carried-over work and any blockers or dependencies.",
                          "displayOrder": 3
                        }
                      ]
                    }
                    """;
            case "sprint-summary" -> """
                    {"title":"Sprint Review: Mock Sprint","overview":"The sprint delivered several meaningful items with steady progress.","deliverySummary":"Delivery remained consistent across the sprint.","qualitySummary":"A few bug fixes improved reliability.","outcomeSummary":"Most planned work landed, with a small amount carried forward."}
                    """;
            case "sprint-themes" -> """
                    [{"name":"Feature Delivery","description":"The sprint centered on shipping customer-facing work.","relatedIssueKeys":["SPR-101","SPR-102"]},
                    {"name":"Stability Improvements","description":"Bug fixes and cleanup improved reliability.","relatedIssueKeys":["SPR-103"]}]
                    """;
            case "sprint-highlights" -> """
                    [{"title":"Delivered key customer-facing work","description":"The team shipped the highest-value stories planned for the sprint.","relatedIssueKeys":["SPR-101","SPR-102"],"category":"FEATURE"},
                    {"title":"Improved platform reliability","description":"The sprint included targeted stability work and bug fixes.","relatedIssueKeys":["SPR-103"],"category":"BUGFIX"}]
                    """;
            case "sprint-blockers" -> """
                    [{"title":"Carried-over dependency work","description":"A small amount of dependency work rolled into the next sprint.","relatedIssueKeys":["SPR-104"],"severity":"MEDIUM"}]
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

    private OpenAiChatRequest buildChatRequest(AiGenerationRequest request, String model) {
        Double temperature = supportsTemperature(model) ? request.temperature() : null;
        return new OpenAiChatRequest(
                model,
                List.of(
                        OpenAiRequestMessageDto.withRole(instructionRole(model), request.prompt().systemPrompt()),
                        OpenAiRequestMessageDto.user(request.prompt().userPrompt())
                ),
                temperature,
                request.maxOutputTokens(),
                request.structuredOutputExpected() ? new OpenAiResponseFormatDto("json_object") : null
        );
    }

    private boolean supportsTemperature(String model) {
        if (model == null || model.isBlank()) {
            return true;
        }
        String normalized = model.trim().toLowerCase();
        return !normalized.startsWith("gpt-5");
    }

    private String instructionRole(String model) {
        if (model == null || model.isBlank()) {
            return "system";
        }
        String normalized = model.trim().toLowerCase();
        if (normalized.startsWith("gpt-5") || normalized.startsWith("o1") || normalized.startsWith("o3") || normalized.startsWith("o4")) {
            return "developer";
        }
        return "system";
    }

    private String extractMessageContent(OpenAiChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return "";
        }
        OpenAiChoiceDto firstChoice = response.choices().getFirst();
        if (firstChoice == null || firstChoice.message() == null) {
            return "";
        }
        return firstChoice.message().textContent();
    }

    private List<List<Double>> extractEmbeddings(JsonNode response) {
        List<List<Double>> embeddings = new ArrayList<>();
        for (JsonNode item : response.path("data")) {
            List<Double> embedding = new ArrayList<>();
            for (JsonNode value : item.path("embedding")) {
                embedding.add(value.asDouble());
            }
            embeddings.add(embedding);
        }
        return embeddings;
    }

    private TokenUsage mapUsage(OpenAiUsageDto usage) {
        if (usage == null) {
            return new TokenUsage(0, 0, 0);
        }
        return new TokenUsage(
                usage.promptTokens() != null ? usage.promptTokens() : 0,
                usage.completionTokens() != null ? usage.completionTokens() : 0,
                usage.totalTokens() != null ? usage.totalTokens() : 0
        );
    }
}

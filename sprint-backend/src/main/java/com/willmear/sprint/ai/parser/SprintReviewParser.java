package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.model.SprintReviewAiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewParser {

    private final ObjectMapper objectMapper;
    private final AiJsonContentExtractor aiJsonContentExtractor;

    public SprintReviewParser(ObjectMapper objectMapper, AiJsonContentExtractor aiJsonContentExtractor) {
        this.objectMapper = objectMapper;
        this.aiJsonContentExtractor = aiJsonContentExtractor;
    }

    public SprintReviewAiResponse parse(AiResponse response) {
        try {
            return objectMapper.readValue(
                    aiJsonContentExtractor.extractJsonObject(response.content()),
                    SprintReviewAiResponse.class
            );
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse sprint review response.", exception);
        }
    }
}

package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.model.PresentationPlanAiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import org.springframework.stereotype.Component;

@Component
public class PresentationPlanParser {

    private final ObjectMapper objectMapper;
    private final AiJsonContentExtractor aiJsonContentExtractor;

    public PresentationPlanParser(ObjectMapper objectMapper, AiJsonContentExtractor aiJsonContentExtractor) {
        this.objectMapper = objectMapper;
        this.aiJsonContentExtractor = aiJsonContentExtractor;
    }

    public PresentationPlanAiResponse parse(AiResponse response) {
        try {
            return objectMapper.readValue(
                    aiJsonContentExtractor.extractJsonObject(response.content()),
                    PresentationPlanAiResponse.class
            );
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse presentation plan response.", exception);
        }
    }
}

package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import org.springframework.stereotype.Component;

@Component
public class SprintSummaryParser {

    private final ObjectMapper objectMapper;
    private final AiJsonContentExtractor aiJsonContentExtractor;

    public SprintSummaryParser(ObjectMapper objectMapper, AiJsonContentExtractor aiJsonContentExtractor) {
        this.objectMapper = objectMapper;
        this.aiJsonContentExtractor = aiJsonContentExtractor;
    }

    public SprintSummary parse(AiResponse response) {
        try {
            return objectMapper.readValue(aiJsonContentExtractor.extractJsonObject(response.content()), SprintSummary.class);
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse sprint summary response.", exception);
        }
    }
}

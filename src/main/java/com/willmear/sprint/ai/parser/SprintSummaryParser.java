package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import com.willmear.sprint.sprintreview.domain.model.SprintSummary;
import org.springframework.stereotype.Component;

@Component
public class SprintSummaryParser {

    private final ObjectMapper objectMapper;

    public SprintSummaryParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SprintSummary parse(AiResponse response) {
        try {
            return objectMapper.readValue(response.content(), SprintSummary.class);
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse sprint summary response.", exception);
        }
    }
}

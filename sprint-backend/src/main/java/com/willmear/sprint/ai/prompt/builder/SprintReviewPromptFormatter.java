package com.willmear.sprint.ai.prompt.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.model.SprintReviewPromptInput;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewPromptFormatter {

    private final ObjectMapper objectMapper;

    public SprintReviewPromptFormatter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String format(SprintReviewPromptInput promptInput) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(promptInput);
        } catch (JsonProcessingException exception) {
            throw new AiPromptBuildException("Failed to serialize sprint review prompt payload.", exception);
        }
    }
}

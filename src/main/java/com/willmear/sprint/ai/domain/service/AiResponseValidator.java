package com.willmear.sprint.ai.domain.service;

import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.common.exception.AiGenerationException;
import org.springframework.stereotype.Component;

@Component
public class AiResponseValidator {

    public AiResponse validate(AiResponse response) {
        if (response == null) {
            throw new AiGenerationException("AI response was null.");
        }
        if (!response.success()) {
            throw new AiGenerationException("AI response indicated failure: " + response.refusalReason());
        }
        if (response.content() == null || response.content().isBlank()) {
            throw new AiGenerationException("AI response content was empty.");
        }
        return response;
    }
}

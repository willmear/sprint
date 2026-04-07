package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ThemeParser {

    private final ObjectMapper objectMapper;

    public ThemeParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<SprintTheme> parse(AiResponse response) {
        try {
            return objectMapper.readValue(response.content(), new TypeReference<List<SprintTheme>>() {
            });
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse theme response.", exception);
        }
    }
}

package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SprintBlockersParser {

    private final ObjectMapper objectMapper;
    private final AiJsonContentExtractor aiJsonContentExtractor;

    public SprintBlockersParser(ObjectMapper objectMapper, AiJsonContentExtractor aiJsonContentExtractor) {
        this.objectMapper = objectMapper;
        this.aiJsonContentExtractor = aiJsonContentExtractor;
    }

    public List<SprintBlocker> parse(AiResponse response) {
        try {
            return objectMapper.readValue(
                    aiJsonContentExtractor.extractJsonArray(response.content()),
                    new TypeReference<List<SprintBlocker>>() {
                    }
            );
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse sprint blockers response.", exception);
        }
    }
}

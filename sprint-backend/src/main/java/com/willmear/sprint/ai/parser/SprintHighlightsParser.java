package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SprintHighlightsParser {

    private final ObjectMapper objectMapper;
    private final AiJsonContentExtractor aiJsonContentExtractor;

    public SprintHighlightsParser(ObjectMapper objectMapper, AiJsonContentExtractor aiJsonContentExtractor) {
        this.objectMapper = objectMapper;
        this.aiJsonContentExtractor = aiJsonContentExtractor;
    }

    public List<SprintHighlight> parse(AiResponse response) {
        try {
            return objectMapper.readValue(
                    aiJsonContentExtractor.extractJsonArray(response.content()),
                    new TypeReference<List<SprintHighlight>>() {
                    }
            );
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse sprint highlights response.", exception);
        }
    }
}

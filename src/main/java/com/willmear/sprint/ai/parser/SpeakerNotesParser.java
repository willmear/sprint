package com.willmear.sprint.ai.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.common.exception.AiResponseParseException;
import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SpeakerNotesParser {

    private final ObjectMapper objectMapper;

    public SpeakerNotesParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<SpeakerNote> parse(AiResponse response) {
        try {
            return objectMapper.readValue(response.content(), new TypeReference<List<SpeakerNote>>() {
            });
        } catch (Exception exception) {
            throw new AiResponseParseException("Unable to parse speaker notes response.", exception);
        }
    }
}

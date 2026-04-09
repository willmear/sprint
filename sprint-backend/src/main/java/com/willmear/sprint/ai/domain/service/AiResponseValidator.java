package com.willmear.sprint.ai.domain.service;

import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.model.SpeakerNoteAiResponse;
import com.willmear.sprint.ai.model.SprintReviewAiResponse;
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

    public SprintReviewAiResponse validateSprintReviewPayload(SprintReviewAiResponse response) {
        if (response == null) {
            throw new AiGenerationException("Parsed sprint review response was null.");
        }
        if (response.summary() == null) {
            throw new AiGenerationException("Parsed sprint review response did not contain a summary.");
        }
        if (isBlank(response.summary().title()) || isBlank(response.summary().overview())) {
            throw new AiGenerationException("Parsed sprint review response is missing summary title or overview.");
        }
        if (response.highlights() == null || response.highlights().isEmpty()) {
            throw new AiGenerationException("Parsed sprint review response did not contain any highlights.");
        }
        if (response.themes() == null || response.blockers() == null || response.speakerNotes() == null) {
            throw new AiGenerationException("Parsed sprint review response is missing one or more required array sections.");
        }
        if (response.highlights().size() > 12 || response.themes().size() > 10 || response.blockers().size() > 10 || response.speakerNotes().size() > 10) {
            throw new AiGenerationException("Parsed sprint review response exceeded practical section limits.");
        }
        boolean invalidSpeakerNoteOrder = response.speakerNotes().stream()
                .map(SpeakerNoteAiResponse::displayOrder)
                .anyMatch(displayOrder -> displayOrder == null || displayOrder < 1 || displayOrder > 20);
        if (invalidSpeakerNoteOrder) {
            throw new AiGenerationException("Parsed sprint review response contained invalid speaker note displayOrder values.");
        }
        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

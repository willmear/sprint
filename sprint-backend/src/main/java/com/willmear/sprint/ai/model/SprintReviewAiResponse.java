package com.willmear.sprint.ai.model;

import java.util.List;

public record SprintReviewAiResponse(
        SprintSummaryAiResponse summary,
        List<SprintThemeAiResponse> themes,
        List<SprintHighlightAiResponse> highlights,
        List<SprintBlockerAiResponse> blockers,
        List<SpeakerNoteAiResponse> speakerNotes
) {
}

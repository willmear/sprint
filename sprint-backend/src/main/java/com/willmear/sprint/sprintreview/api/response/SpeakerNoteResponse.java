package com.willmear.sprint.sprintreview.api.response;

public record SpeakerNoteResponse(
        String section,
        String note,
        Integer displayOrder
) {
}

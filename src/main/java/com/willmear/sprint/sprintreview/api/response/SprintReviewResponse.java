package com.willmear.sprint.sprintreview.api.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SprintReviewResponse(
        UUID id,
        UUID workspaceId,
        Long externalSprintId,
        String sprintName,
        SprintSummaryResponse summary,
        List<SprintThemeResponse> themes,
        List<SprintHighlightResponse> highlights,
        List<SprintBlockerResponse> blockers,
        List<SpeakerNoteResponse> speakerNotes,
        Instant generatedAt,
        String generationSource,
        String status
) {
}

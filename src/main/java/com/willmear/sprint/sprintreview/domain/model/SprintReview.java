package com.willmear.sprint.sprintreview.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SprintReview(
        UUID id,
        UUID workspaceId,
        Long externalSprintId,
        String sprintName,
        SprintSummary summary,
        List<SprintTheme> themes,
        List<SprintHighlight> highlights,
        List<SprintBlocker> blockers,
        List<SpeakerNote> speakerNotes,
        Instant generatedAt,
        String generationSource,
        String status
) {
}

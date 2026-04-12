package com.willmear.sprint.presentationplan.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PresentationPlan(
        UUID workspaceId,
        String referenceType,
        String referenceId,
        String title,
        String subtitle,
        List<PlannedSlide> slides,
        Instant createdAt
) {
}

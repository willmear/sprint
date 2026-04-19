package com.willmear.sprint.credits.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserDailyCreditUsage(
        UUID id,
        UUID userId,
        LocalDate usageDate,
        int generationCount,
        Instant createdAt,
        Instant updatedAt
) {
}

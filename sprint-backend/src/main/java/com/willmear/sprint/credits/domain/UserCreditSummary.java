package com.willmear.sprint.credits.domain;

import java.time.LocalDate;
import java.util.UUID;

public record UserCreditSummary(
        UUID userId,
        int dailyLimit,
        int usedToday,
        int remainingToday,
        LocalDate usageDate,
        boolean canGenerate
) {
}

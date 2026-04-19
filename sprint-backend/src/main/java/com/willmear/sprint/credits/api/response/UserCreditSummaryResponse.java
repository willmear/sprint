package com.willmear.sprint.credits.api.response;

import java.time.LocalDate;
import java.util.UUID;

public record UserCreditSummaryResponse(
        UUID userId,
        int dailyLimit,
        int usedToday,
        int remainingToday,
        LocalDate usageDate,
        boolean canGenerate
) {
}

package com.willmear.sprint.credits.domain;

public record UserCreditPolicy(
        int dailyGenerationLimit,
        boolean enabled
) {
}

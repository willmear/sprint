package com.willmear.sprint.common.exception;

import java.time.LocalDate;
import java.util.UUID;

public class InsufficientDailyCreditsException extends RuntimeException {

    private final UUID userId;
    private final LocalDate usageDate;
    private final int dailyLimit;
    private final int usedToday;
    private final int remainingToday;

    public InsufficientDailyCreditsException(UUID userId, LocalDate usageDate, int dailyLimit, int usedToday) {
        super("You have reached your sprint review generation limit for today.");
        this.userId = userId;
        this.usageDate = usageDate;
        this.dailyLimit = dailyLimit;
        this.usedToday = usedToday;
        this.remainingToday = Math.max(0, dailyLimit - usedToday);
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public int getUsedToday() {
        return usedToday;
    }

    public int getRemainingToday() {
        return remainingToday;
    }
}

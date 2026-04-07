package com.willmear.sprint.jobs.domain;

public record JobExecutionResult(
        boolean success,
        String message,
        String errorCode
) {

    public static JobExecutionResult success(String message) {
        return new JobExecutionResult(true, message, null);
    }

    public static JobExecutionResult failure(String message, String errorCode) {
        return new JobExecutionResult(false, message, errorCode);
    }
}

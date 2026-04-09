package com.willmear.sprint.ai.model;

public record SprintSummaryAiResponse(
        String title,
        String overview,
        String deliverySummary,
        String qualitySummary,
        String outcomeSummary
) {
}

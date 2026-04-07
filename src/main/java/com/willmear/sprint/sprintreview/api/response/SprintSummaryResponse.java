package com.willmear.sprint.sprintreview.api.response;

public record SprintSummaryResponse(
        String title,
        String overview,
        String deliverySummary,
        String qualitySummary,
        String outcomeSummary
) {
}

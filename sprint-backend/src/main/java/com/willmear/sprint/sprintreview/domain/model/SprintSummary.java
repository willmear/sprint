package com.willmear.sprint.sprintreview.domain.model;

public record SprintSummary(
        String title,
        String overview,
        String deliverySummary,
        String qualitySummary,
        String outcomeSummary
) {
}

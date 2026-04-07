package com.willmear.sprint.sprintreview.domain.service;

import com.willmear.sprint.sprintreview.domain.model.IssueSummary;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SprintThemeExtractor {

    public List<SprintTheme> extract(SprintContext context, int maxThemes) {
        List<SprintTheme> themes = new ArrayList<>();

        List<IssueSummary> featureIssues = context.completedIssues().stream()
                .filter(issue -> !issue.bugFix() && !issue.technicalWork())
                .toList();
        if (!featureIssues.isEmpty()) {
            themes.add(new SprintTheme(
                    "Feature Delivery",
                    "The sprint focused on delivering user-facing functionality across " + featureIssues.size() + " issues.",
                    featureIssues.stream().map(IssueSummary::issueKey).limit(5).toList()
            ));
        }

        if (!context.bugFixes().isEmpty()) {
            themes.add(new SprintTheme(
                    "Stability and Bug Fixes",
                    "The team addressed " + context.bugFixes().size() + " bug-related items to improve stability.",
                    context.bugFixes().stream().map(IssueSummary::issueKey).limit(5).toList()
            ));
        }

        if (!context.technicalImprovements().isEmpty()) {
            themes.add(new SprintTheme(
                    "Technical Improvements",
                    "The sprint also included " + context.technicalImprovements().size() + " technical improvement items.",
                    context.technicalImprovements().stream().map(IssueSummary::issueKey).limit(5).toList()
            ));
        }

        if (!context.carriedOverIssues().isEmpty() && themes.size() < maxThemes) {
            themes.add(new SprintTheme(
                    "Carryover and Risks",
                    "Several issues remain open and should be tracked into the next sprint.",
                    context.carriedOverIssues().stream().map(IssueSummary::issueKey).limit(5).toList()
            ));
        }

        return themes.stream().limit(maxThemes).toList();
    }
}

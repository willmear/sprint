package com.willmear.sprint.sprintreview.domain.service;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.common.exception.SprintReviewGenerationException;
import com.willmear.sprint.sprintreview.application.support.SprintDataBundle;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SprintDomainServicesTest {

    private final SprintContextAssembler sprintContextAssembler = new SprintContextAssembler();
    private final SprintReviewValidator sprintReviewValidator = new SprintReviewValidator();
    private final SprintThemeExtractor sprintThemeExtractor = new SprintThemeExtractor();

    @Test
    void shouldAssembleSprintContextWithDerivedBucketsAndBlockers() {
        SprintDataBundle bundle = TestSprintReviewFactory.bundleWithJiraData();

        SprintContext context = sprintContextAssembler.assemble(bundle);

        assertThat(context.completedIssues()).hasSize(2);
        assertThat(context.inProgressIssues()).hasSize(1);
        assertThat(context.carriedOverIssues()).hasSize(1);
        assertThat(context.bugFixes()).hasSize(1);
        assertThat(context.technicalImprovements()).hasSize(1);
        assertThat(context.notableComments()).hasSize(2);
        assertThat(context.blockers()).anyMatch(value -> value.contains("carried over"));
    }

    @Test
    void shouldValidateSummaryAndHighlightsPresence() {
        assertThat(sprintReviewValidator.validate(TestSprintReviewFactory.reviewWithHighlight())).isNotNull();

        assertThatThrownBy(() -> sprintReviewValidator.validate(TestSprintReviewFactory.reviewMissingOverview()))
                .isInstanceOf(SprintReviewGenerationException.class);
        assertThatThrownBy(() -> sprintReviewValidator.validate(TestSprintReviewFactory.reviewMissingHighlights()))
                .isInstanceOf(SprintReviewGenerationException.class);
    }

    @Test
    void shouldExtractThemesWithinMaxLimit() {
        SprintContext context = TestSprintReviewFactory.contextWithIssues();

        List<com.willmear.sprint.sprintreview.domain.model.SprintTheme> themes = sprintThemeExtractor.extract(context, 2);

        assertThat(themes).hasSize(2);
        assertThat(themes).extracting(com.willmear.sprint.sprintreview.domain.model.SprintTheme::name)
                .contains("Feature Delivery", "Stability and Bug Fixes");
    }
}

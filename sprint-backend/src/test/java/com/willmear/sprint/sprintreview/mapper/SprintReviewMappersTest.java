package com.willmear.sprint.sprintreview.mapper;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.sprintreview.api.response.SprintContextResponse;
import com.willmear.sprint.sprintreview.api.response.SprintReviewResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SprintReviewMappersTest {

    private final IssueSummaryMapper issueSummaryMapper = new IssueSummaryMapper(new IssueCommentSummaryMapper());
    private final SprintContextMapper sprintContextMapper = new SprintContextMapper(issueSummaryMapper);
    private final SprintReviewMapper sprintReviewMapper = new SprintReviewMapper();

    @Test
    void shouldMapSprintContextToResponse() {
        SprintContextResponse response = sprintContextMapper.toResponse(TestSprintReviewFactory.contextWithIssues());

        assertThat(response.completedIssues()).hasSize(2);
        assertThat(response.carriedOverIssues().getFirst().comments()).hasSize(1);
        assertThat(response.notableComments()).contains("Blocked on upstream dependency");
        assertThat(response.totalIssueCount()).isEqualTo(4);
    }

    @Test
    void shouldMapSprintReviewAndJobToResponses() {
        SprintReviewResponse response = sprintReviewMapper.toResponse(TestSprintReviewFactory.reviewWithHighlight());
        Job job = new Job(UUID.randomUUID(), UUID.randomUUID(), JobType.GENERATE_SPRINT_REVIEW, JobStatus.PENDING,
                "default", null, 0, 3, Instant.now(), null, null, null, null, null, null, null, Instant.now(), Instant.now());

        assertThat(response.summary().title()).isEqualTo("Sprint Review");
        assertThat(response.highlights()).hasSize(1);
        assertThat(sprintReviewMapper.toJobResponse(job).status()).isEqualTo("PENDING");
    }
}

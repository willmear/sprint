package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.jira.domain.model.JiraSprint;
import com.willmear.sprint.sprintreview.application.support.SprintDataBundle;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.port.ReviewContextEnricherPort;
import com.willmear.sprint.sprintreview.domain.port.SprintDataProviderPort;
import com.willmear.sprint.sprintreview.domain.service.SprintContextAssembler;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BuildSprintContextUseCaseTest {

    private final SprintDataProviderPort sprintDataProviderPort = mock(SprintDataProviderPort.class);
    private final SprintContextAssembler sprintContextAssembler = mock(SprintContextAssembler.class);
    private final ReviewContextEnricherPort reviewContextEnricherPort = mock(ReviewContextEnricherPort.class);

    @Test
    void shouldBuildAndEnrichSprintContext() {
        BuildSprintContextUseCase useCase = new BuildSprintContextUseCase(
                sprintDataProviderPort,
                sprintContextAssembler,
                reviewContextEnricherPort
        );
        UUID workspaceId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        SprintDataBundle bundle = new SprintDataBundle(
                workspaceId,
                connectionId,
                new JiraSprint(9L, workspaceId, connectionId, 12L, "Sprint 9", "Goal", "ACTIVE", Instant.now(), Instant.now(), null, Instant.now()),
                List.of(),
                List.of(),
                List.of()
        );
        SprintContext assembled = TestSprintReviewFactory.context(workspaceId, connectionId, 9L);
        SprintContext enriched = new SprintContext(
                assembled.workspaceId(),
                assembled.jiraConnectionId(),
                assembled.externalSprintId(),
                assembled.sprintName(),
                assembled.sprintGoal(),
                assembled.sprintState(),
                assembled.sprintStartDate(),
                assembled.sprintEndDate(),
                assembled.completedIssues(),
                assembled.inProgressIssues(),
                assembled.carriedOverIssues(),
                assembled.bugFixes(),
                assembled.technicalImprovements(),
                assembled.allIssues(),
                List.of("enriched comment"),
                assembled.blockers(),
                assembled.totalIssueCount(),
                assembled.totalCommentCount(),
                assembled.totalChangelogCount(),
                assembled.assembledAt()
        );

        when(sprintDataProviderPort.getSprintData(workspaceId, 9L, true, false)).thenReturn(bundle);
        when(sprintContextAssembler.assemble(bundle)).thenReturn(assembled);
        when(reviewContextEnricherPort.enrich(assembled)).thenReturn(enriched);

        SprintContext result = useCase.build(workspaceId, 9L, true, false);

        assertThat(result).isEqualTo(enriched);
        verify(sprintDataProviderPort).getSprintData(workspaceId, 9L, true, false);
        verify(sprintContextAssembler).assemble(bundle);
        verify(reviewContextEnricherPort).enrich(assembled);
    }
}

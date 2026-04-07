package com.willmear.sprint.retrieval.application;

import com.willmear.sprint.config.RetrievalProperties;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResultSet;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.port.ReviewContextEnricherPort;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class RetrievalBackedReviewContextEnricher implements ReviewContextEnricherPort {

    private final SearchSprintContextUseCase searchSprintContextUseCase;
    private final RetrievalProperties retrievalProperties;

    public RetrievalBackedReviewContextEnricher(
            SearchSprintContextUseCase searchSprintContextUseCase,
            RetrievalProperties retrievalProperties
    ) {
        this.searchSprintContextUseCase = searchSprintContextUseCase;
        this.retrievalProperties = retrievalProperties;
    }

    @Override
    public SprintContext enrich(SprintContext sprintContext) {
        if (!retrievalProperties.enabled() || !retrievalProperties.enrichSprintReviewContext()) {
            return sprintContext;
        }

        try {
            RetrievalResultSet resultSet = searchSprintContextUseCase.search(new RetrievalQuery(
                    sprintContext.workspaceId(),
                    "key sprint accomplishments and blockers",
                    retrievalProperties.defaultTopK(),
                    sprintContext.externalSprintId(),
                    null,
                    null,
                    true,
                    true
            ));

            List<String> enrichedComments = new ArrayList<>(sprintContext.notableComments());
            List<String> enrichedBlockers = new ArrayList<>(sprintContext.blockers());
            resultSet.results().stream()
                    .map(result -> result.contentSnippet())
                    .filter(snippet -> snippet != null && !snippet.isBlank())
                    .limit(3)
                    .forEach(snippet -> {
                        if (!enrichedComments.contains(snippet)) {
                            enrichedComments.add(snippet);
                        }
                        String normalized = snippet.toLowerCase();
                        if ((normalized.contains("blocked") || normalized.contains("waiting") || normalized.contains("dependency"))
                                && !enrichedBlockers.contains(snippet)) {
                            enrichedBlockers.add(snippet);
                        }
                    });

            return new SprintContext(
                    sprintContext.workspaceId(),
                    sprintContext.jiraConnectionId(),
                    sprintContext.externalSprintId(),
                    sprintContext.sprintName(),
                    sprintContext.sprintGoal(),
                    sprintContext.sprintState(),
                    sprintContext.sprintStartDate(),
                    sprintContext.sprintEndDate(),
                    sprintContext.completedIssues(),
                    sprintContext.inProgressIssues(),
                    sprintContext.carriedOverIssues(),
                    sprintContext.bugFixes(),
                    sprintContext.technicalImprovements(),
                    sprintContext.allIssues(),
                    List.copyOf(enrichedComments),
                    List.copyOf(enrichedBlockers),
                    sprintContext.totalIssueCount(),
                    sprintContext.totalCommentCount(),
                    sprintContext.totalChangelogCount(),
                    sprintContext.assembledAt()
            );
        } catch (RuntimeException exception) {
            // TODO: Add more explicit fallback/telemetry once retrieval observability is introduced.
            return sprintContext;
        }
    }
}

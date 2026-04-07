package com.willmear.sprint.sprintreview.application.support;

import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.port.ReviewContextEnricherPort;
import org.springframework.stereotype.Component;

@Component
public class NoOpReviewContextEnricher implements ReviewContextEnricherPort {

    @Override
    public SprintContext enrich(SprintContext sprintContext) {
        // TODO: Add retrieval-based enrichment when semantic search is introduced.
        return sprintContext;
    }
}

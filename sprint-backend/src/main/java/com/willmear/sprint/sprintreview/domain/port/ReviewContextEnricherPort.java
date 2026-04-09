package com.willmear.sprint.sprintreview.domain.port;

import com.willmear.sprint.sprintreview.domain.model.SprintContext;

public interface ReviewContextEnricherPort {

    SprintContext enrich(SprintContext sprintContext);
}

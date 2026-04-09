package com.willmear.sprint.sprintreview.domain.port;

import com.willmear.sprint.sprintreview.domain.model.SprintReview;

public interface ArtifactWriterPort {

    void write(SprintReview sprintReview);
}

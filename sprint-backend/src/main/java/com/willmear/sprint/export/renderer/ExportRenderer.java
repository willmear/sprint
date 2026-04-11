package com.willmear.sprint.export.renderer;

import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;

public interface ExportRenderer {

    ExportFormat supports();

    ExportPayload render(SprintReview sprintReview);
}

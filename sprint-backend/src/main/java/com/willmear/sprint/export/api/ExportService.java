package com.willmear.sprint.export.api;

import com.willmear.sprint.export.domain.ExportFormat;
import com.willmear.sprint.export.domain.ExportPayload;
import java.util.UUID;

public interface ExportService {

    ExportPayload exportLatestSprintReview(UUID workspaceId, Long sprintId, ExportFormat format);

    ExportPayload exportArtifact(UUID artifactId, ExportFormat format);
}

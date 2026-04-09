package com.willmear.sprint.sprintreview.domain.port;

import com.willmear.sprint.sprintreview.application.support.SprintDataBundle;
import java.util.UUID;

public interface SprintDataProviderPort {

    SprintDataBundle getSprintData(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeChangelog);
}

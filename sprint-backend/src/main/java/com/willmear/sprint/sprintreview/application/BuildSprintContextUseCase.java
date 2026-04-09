package com.willmear.sprint.sprintreview.application;

import com.willmear.sprint.sprintreview.application.support.SprintDataBundle;
import com.willmear.sprint.sprintreview.domain.model.SprintContext;
import com.willmear.sprint.sprintreview.domain.port.ReviewContextEnricherPort;
import com.willmear.sprint.sprintreview.domain.port.SprintDataProviderPort;
import com.willmear.sprint.sprintreview.domain.service.SprintContextAssembler;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BuildSprintContextUseCase {

    private final SprintDataProviderPort sprintDataProviderPort;
    private final SprintContextAssembler sprintContextAssembler;
    private final ReviewContextEnricherPort reviewContextEnricherPort;

    public BuildSprintContextUseCase(
            SprintDataProviderPort sprintDataProviderPort,
            SprintContextAssembler sprintContextAssembler,
            ReviewContextEnricherPort reviewContextEnricherPort
    ) {
        this.sprintDataProviderPort = sprintDataProviderPort;
        this.sprintContextAssembler = sprintContextAssembler;
        this.reviewContextEnricherPort = reviewContextEnricherPort;
    }

    public SprintContext build(UUID workspaceId, Long externalSprintId, boolean includeComments, boolean includeChangelog) {
        SprintDataBundle dataBundle = sprintDataProviderPort.getSprintData(workspaceId, externalSprintId, includeComments, includeChangelog);
        SprintContext context = sprintContextAssembler.assemble(dataBundle);
        return reviewContextEnricherPort.enrich(context);
    }
}

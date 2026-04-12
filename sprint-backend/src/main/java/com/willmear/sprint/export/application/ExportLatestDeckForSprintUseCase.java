package com.willmear.sprint.export.application;

import com.willmear.sprint.export.domain.BinaryExportPayload;
import com.willmear.sprint.export.mapper.PresentationDeckToPowerPointMapper;
import com.willmear.sprint.export.renderer.PowerPointExportRenderer;
import com.willmear.sprint.presentation.api.PresentationDeckService;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExportLatestDeckForSprintUseCase {

    private final PresentationDeckService presentationDeckService;
    private final PresentationDeckToPowerPointMapper presentationDeckToPowerPointMapper;
    private final PowerPointExportRenderer powerPointExportRenderer;

    public ExportLatestDeckForSprintUseCase(
            PresentationDeckService presentationDeckService,
            PresentationDeckToPowerPointMapper presentationDeckToPowerPointMapper,
            PowerPointExportRenderer powerPointExportRenderer
    ) {
        this.presentationDeckService = presentationDeckService;
        this.presentationDeckToPowerPointMapper = presentationDeckToPowerPointMapper;
        this.powerPointExportRenderer = powerPointExportRenderer;
    }

    public BinaryExportPayload export(UUID workspaceId, Long sprintId) {
        PresentationDeck deck = presentationDeckService.getLatestDeckForSprint(workspaceId, sprintId);
        return powerPointExportRenderer.render(deck, presentationDeckToPowerPointMapper.toRenderModel(deck));
    }
}

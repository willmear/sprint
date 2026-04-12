package com.willmear.sprint.export.application;

import com.willmear.sprint.export.domain.BinaryExportPayload;
import com.willmear.sprint.export.mapper.PresentationDeckToPowerPointMapper;
import com.willmear.sprint.export.renderer.PowerPointExportRenderer;
import com.willmear.sprint.presentation.api.PresentationDeckService;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExportPresentationDeckAsPowerPointUseCase {

    private final PresentationDeckService presentationDeckService;
    private final PresentationDeckToPowerPointMapper presentationDeckToPowerPointMapper;
    private final PowerPointExportRenderer powerPointExportRenderer;

    public ExportPresentationDeckAsPowerPointUseCase(
            PresentationDeckService presentationDeckService,
            PresentationDeckToPowerPointMapper presentationDeckToPowerPointMapper,
            PowerPointExportRenderer powerPointExportRenderer
    ) {
        this.presentationDeckService = presentationDeckService;
        this.presentationDeckToPowerPointMapper = presentationDeckToPowerPointMapper;
        this.powerPointExportRenderer = powerPointExportRenderer;
    }

    public BinaryExportPayload export(UUID workspaceId, UUID deckId) {
        PresentationDeck deck = presentationDeckService.getDeck(workspaceId, deckId);
        return powerPointExportRenderer.render(deck, presentationDeckToPowerPointMapper.toRenderModel(deck));
    }
}

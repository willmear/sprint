package com.willmear.sprint.presentationplan.mapper;

import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.template.DeckLayoutEngine;
import com.willmear.sprint.presentationplan.domain.PresentationPlan;
import org.springframework.stereotype.Component;

@Component
public class PresentationPlanToPresentationDeckMapper {

    private final DeckLayoutEngine deckLayoutEngine;

    public PresentationPlanToPresentationDeckMapper(DeckLayoutEngine deckLayoutEngine) {
        this.deckLayoutEngine = deckLayoutEngine;
    }

    public PresentationDeck toDeck(Artifact artifact, PresentationPlan presentationPlan) {
        return toDeck(artifact, presentationPlan, null);
    }

    public PresentationDeck toDeck(Artifact artifact, PresentationPlan presentationPlan, String themeId) {
        return deckLayoutEngine.layout(artifact, presentationPlan, themeId);
    }
}

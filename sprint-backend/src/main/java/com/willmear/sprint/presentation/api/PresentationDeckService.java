package com.willmear.sprint.presentation.api;

import com.willmear.sprint.presentation.api.request.AddSlideRequest;
import com.willmear.sprint.presentation.api.request.ReorderSlidesRequest;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.request.UpdateSlideRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import java.util.UUID;

public interface PresentationDeckService {

    PresentationDeck createOrGetDeck(UUID workspaceId, Long sprintId);

    PresentationDeck getLatestDeckForSprint(UUID workspaceId, Long sprintId);

    PresentationDeck getDeck(UUID workspaceId, UUID deckId);

    PresentationDeck saveDeck(UUID workspaceId, UUID deckId, UpdateDeckRequest request);

    PresentationDeck updateSlide(UUID workspaceId, UUID deckId, UUID slideId, UpdateSlideRequest request);

    PresentationDeck reorderSlides(UUID workspaceId, UUID deckId, ReorderSlidesRequest request);

    PresentationDeck addSlide(UUID workspaceId, UUID deckId, AddSlideRequest request);

    PresentationDeck duplicateSlide(UUID workspaceId, UUID deckId, UUID slideId);

    void deleteSlide(UUID workspaceId, UUID deckId, UUID slideId);
}

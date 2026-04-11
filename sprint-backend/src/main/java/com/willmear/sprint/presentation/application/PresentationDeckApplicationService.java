package com.willmear.sprint.presentation.application;

import com.willmear.sprint.presentation.api.PresentationDeckService;
import com.willmear.sprint.presentation.api.request.AddSlideRequest;
import com.willmear.sprint.presentation.api.request.ReorderSlidesRequest;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.request.UpdateSlideRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PresentationDeckApplicationService implements PresentationDeckService {

    private final CreateDeckFromSprintReviewUseCase createDeckFromSprintReviewUseCase;
    private final GetDeckUseCase getDeckUseCase;
    private final GetLatestDeckForSprintUseCase getLatestDeckForSprintUseCase;
    private final SaveDeckUseCase saveDeckUseCase;
    private final UpdateSlideUseCase updateSlideUseCase;
    private final ReorderSlidesUseCase reorderSlidesUseCase;
    private final AddSlideUseCase addSlideUseCase;
    private final DeleteSlideUseCase deleteSlideUseCase;
    private final DuplicateSlideUseCase duplicateSlideUseCase;

    public PresentationDeckApplicationService(
            CreateDeckFromSprintReviewUseCase createDeckFromSprintReviewUseCase,
            GetDeckUseCase getDeckUseCase,
            GetLatestDeckForSprintUseCase getLatestDeckForSprintUseCase,
            SaveDeckUseCase saveDeckUseCase,
            UpdateSlideUseCase updateSlideUseCase,
            ReorderSlidesUseCase reorderSlidesUseCase,
            AddSlideUseCase addSlideUseCase,
            DeleteSlideUseCase deleteSlideUseCase,
            DuplicateSlideUseCase duplicateSlideUseCase
    ) {
        this.createDeckFromSprintReviewUseCase = createDeckFromSprintReviewUseCase;
        this.getDeckUseCase = getDeckUseCase;
        this.getLatestDeckForSprintUseCase = getLatestDeckForSprintUseCase;
        this.saveDeckUseCase = saveDeckUseCase;
        this.updateSlideUseCase = updateSlideUseCase;
        this.reorderSlidesUseCase = reorderSlidesUseCase;
        this.addSlideUseCase = addSlideUseCase;
        this.deleteSlideUseCase = deleteSlideUseCase;
        this.duplicateSlideUseCase = duplicateSlideUseCase;
    }

    @Override
    public PresentationDeck createOrGetDeck(UUID workspaceId, Long sprintId) {
        return createDeckFromSprintReviewUseCase.createOrGet(workspaceId, sprintId);
    }

    @Override
    public PresentationDeck getLatestDeckForSprint(UUID workspaceId, Long sprintId) {
        return getLatestDeckForSprintUseCase.get(workspaceId, sprintId);
    }

    @Override
    public PresentationDeck getDeck(UUID workspaceId, UUID deckId) {
        return getDeckUseCase.get(workspaceId, deckId);
    }

    @Override
    public PresentationDeck saveDeck(UUID workspaceId, UUID deckId, UpdateDeckRequest request) {
        return saveDeckUseCase.save(workspaceId, deckId, request);
    }

    @Override
    public PresentationDeck updateSlide(UUID workspaceId, UUID deckId, UUID slideId, UpdateSlideRequest request) {
        return updateSlideUseCase.update(workspaceId, deckId, slideId, request);
    }

    @Override
    public PresentationDeck reorderSlides(UUID workspaceId, UUID deckId, ReorderSlidesRequest request) {
        return reorderSlidesUseCase.reorder(workspaceId, deckId, request);
    }

    @Override
    public PresentationDeck addSlide(UUID workspaceId, UUID deckId, AddSlideRequest request) {
        return addSlideUseCase.add(workspaceId, deckId, request);
    }

    @Override
    public PresentationDeck duplicateSlide(UUID workspaceId, UUID deckId, UUID slideId) {
        return duplicateSlideUseCase.duplicate(workspaceId, deckId, slideId);
    }

    @Override
    public void deleteSlide(UUID workspaceId, UUID deckId, UUID slideId) {
        deleteSlideUseCase.delete(workspaceId, deckId, slideId);
    }
}

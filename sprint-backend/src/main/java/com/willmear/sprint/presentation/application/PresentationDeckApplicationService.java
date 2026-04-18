package com.willmear.sprint.presentation.application;

import com.willmear.sprint.presentation.api.PresentationDeckService;
import com.willmear.sprint.presentation.api.request.AddSlideRequest;
import com.willmear.sprint.presentation.api.request.ReorderSlidesRequest;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.request.UpdateSlideRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.workspace.application.WorkspaceAuthorizationService;
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
    private final WorkspaceAuthorizationService workspaceAuthorizationService;

    public PresentationDeckApplicationService(
            CreateDeckFromSprintReviewUseCase createDeckFromSprintReviewUseCase,
            GetDeckUseCase getDeckUseCase,
            GetLatestDeckForSprintUseCase getLatestDeckForSprintUseCase,
            SaveDeckUseCase saveDeckUseCase,
            UpdateSlideUseCase updateSlideUseCase,
            ReorderSlidesUseCase reorderSlidesUseCase,
            AddSlideUseCase addSlideUseCase,
            DeleteSlideUseCase deleteSlideUseCase,
            DuplicateSlideUseCase duplicateSlideUseCase,
            WorkspaceAuthorizationService workspaceAuthorizationService
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
        this.workspaceAuthorizationService = workspaceAuthorizationService;
    }

    @Override
    public PresentationDeck createOrGetDeck(UUID workspaceId, Long sprintId, String themeId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return createDeckFromSprintReviewUseCase.createOrGet(workspaceId, sprintId, themeId);
    }

    @Override
    public PresentationDeck getLatestDeckForSprint(UUID workspaceId, Long sprintId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return getLatestDeckForSprintUseCase.get(workspaceId, sprintId);
    }

    @Override
    public PresentationDeck getDeck(UUID workspaceId, UUID deckId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return getDeckUseCase.get(workspaceId, deckId);
    }

    @Override
    public PresentationDeck saveDeck(UUID workspaceId, UUID deckId, UpdateDeckRequest request) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return saveDeckUseCase.save(workspaceId, deckId, request);
    }

    @Override
    public PresentationDeck updateSlide(UUID workspaceId, UUID deckId, UUID slideId, UpdateSlideRequest request) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return updateSlideUseCase.update(workspaceId, deckId, slideId, request);
    }

    @Override
    public PresentationDeck reorderSlides(UUID workspaceId, UUID deckId, ReorderSlidesRequest request) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return reorderSlidesUseCase.reorder(workspaceId, deckId, request);
    }

    @Override
    public PresentationDeck addSlide(UUID workspaceId, UUID deckId, AddSlideRequest request) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return addSlideUseCase.add(workspaceId, deckId, request);
    }

    @Override
    public PresentationDeck duplicateSlide(UUID workspaceId, UUID deckId, UUID slideId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        return duplicateSlideUseCase.duplicate(workspaceId, deckId, slideId);
    }

    @Override
    public void deleteSlide(UUID workspaceId, UUID deckId, UUID slideId) {
        workspaceAuthorizationService.ensureCanAccessWorkspace(workspaceId);
        deleteSlideUseCase.delete(workspaceId, deckId, slideId);
    }
}

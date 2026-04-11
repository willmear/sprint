package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.InvalidSlideOrderException;
import com.willmear.sprint.common.exception.PresentationDeckCreationException;
import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SaveDeckUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;

    public SaveDeckUseCase(PresentationDeckRepository presentationDeckRepository, PresentationDeckMapper presentationDeckMapper) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @Transactional
    public PresentationDeck save(UUID workspaceId, UUID deckId, UpdateDeckRequest request) {
        validateSlides(request);
        var existing = presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
        PresentationDeck toSave = presentationDeckMapper.toDomain(existing, request);
        try {
            return presentationDeckMapper.toDomain(presentationDeckRepository.save(presentationDeckMapper.toEntity(toSave)));
        } catch (RuntimeException exception) {
            throw new PresentationDeckCreationException("Failed to save slide deck " + deckId + ".", exception);
        }
    }

    private void validateSlides(UpdateDeckRequest request) {
        if (request.slides().isEmpty()) {
            throw new InvalidSlideOrderException("A deck must contain at least one slide.");
        }
        HashSet<UUID> seenIds = new HashSet<>();
        request.slides().stream()
                .map(UpdateDeckRequest.UpdateDeckSlideRequest::id)
                .filter(java.util.Objects::nonNull)
                .forEach(id -> {
                    if (!seenIds.add(id)) {
                        throw new InvalidSlideOrderException("Slide ids must be unique within a deck save request.");
                    }
                });
    }
}

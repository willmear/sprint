package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.InvalidSlideOrderException;
import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.common.exception.PresentationSlideNotFoundException;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeleteSlideUseCase {

    private final PresentationDeckRepository presentationDeckRepository;

    public DeleteSlideUseCase(PresentationDeckRepository presentationDeckRepository) {
        this.presentationDeckRepository = presentationDeckRepository;
    }

    @Transactional
    public void delete(UUID workspaceId, UUID deckId, UUID slideId) {
        var deck = presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
        if (deck.getSlides().size() == 1) {
            throw new InvalidSlideOrderException("A deck must keep at least one slide.");
        }
        boolean removed = deck.getSlides().removeIf(slide -> slide.getId().equals(slideId));
        if (!removed) {
            throw new PresentationSlideNotFoundException(deckId, slideId);
        }
        for (int index = 0; index < deck.getSlides().size(); index++) {
            deck.getSlides().get(index).setSlideOrder(index);
        }
        presentationDeckRepository.save(deck);
    }
}

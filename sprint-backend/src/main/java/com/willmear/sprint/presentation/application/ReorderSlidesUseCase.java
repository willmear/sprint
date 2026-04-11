package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.InvalidSlideOrderException;
import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.presentation.api.request.ReorderSlidesRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReorderSlidesUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;

    public ReorderSlidesUseCase(PresentationDeckRepository presentationDeckRepository, PresentationDeckMapper presentationDeckMapper) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @Transactional
    public PresentationDeck reorder(UUID workspaceId, UUID deckId, ReorderSlidesRequest request) {
        var deck = presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
        if (request.slideIds().size() != deck.getSlides().size()) {
            throw new InvalidSlideOrderException("Reorder request must include every slide exactly once.");
        }
        HashSet<UUID> requestedIds = new HashSet<>(request.slideIds());
        if (requestedIds.size() != deck.getSlides().size()) {
            throw new InvalidSlideOrderException("Reorder request contains duplicate slide ids.");
        }
        if (!deck.getSlides().stream().map(slide -> slide.getId()).allMatch(requestedIds::contains)) {
            throw new InvalidSlideOrderException("Reorder request includes unknown slide ids.");
        }
        for (int index = 0; index < request.slideIds().size(); index++) {
            UUID slideId = request.slideIds().get(index);
            for (var slide : deck.getSlides()) {
                if (slide.getId().equals(slideId)) {
                    slide.setSlideOrder(index);
                    break;
                }
            }
        }
        return presentationDeckMapper.toDomain(presentationDeckRepository.save(deck));
    }
}

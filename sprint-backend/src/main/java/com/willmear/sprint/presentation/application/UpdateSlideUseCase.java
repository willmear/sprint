package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.common.exception.PresentationSlideNotFoundException;
import com.willmear.sprint.presentation.api.request.UpdateSlideRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateSlideUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;
    private final PresentationSlideMapper presentationSlideMapper;

    public UpdateSlideUseCase(
            PresentationDeckRepository presentationDeckRepository,
            PresentationDeckMapper presentationDeckMapper,
            PresentationSlideMapper presentationSlideMapper
    ) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
        this.presentationSlideMapper = presentationSlideMapper;
    }

    @Transactional
    public PresentationDeck update(UUID workspaceId, UUID deckId, UUID slideId, UpdateSlideRequest request) {
        var deck = presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
        var slide = deck.getSlides().stream()
                .filter(candidate -> candidate.getId().equals(slideId))
                .findFirst()
                .orElseThrow(() -> new PresentationSlideNotFoundException(deckId, slideId));
        presentationSlideMapper.apply(slide, request);
        return presentationDeckMapper.toDomain(presentationDeckRepository.save(deck));
    }
}

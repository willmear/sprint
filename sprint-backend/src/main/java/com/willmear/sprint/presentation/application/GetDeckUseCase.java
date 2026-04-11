package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetDeckUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;

    public GetDeckUseCase(PresentationDeckRepository presentationDeckRepository, PresentationDeckMapper presentationDeckMapper) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @Transactional
    public PresentationDeck get(UUID workspaceId, UUID deckId) {
        return presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .map(presentationDeckMapper::toDomain)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
    }
}

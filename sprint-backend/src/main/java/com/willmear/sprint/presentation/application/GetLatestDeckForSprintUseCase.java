package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.mapper.SprintReviewToPresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetLatestDeckForSprintUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;

    public GetLatestDeckForSprintUseCase(
            PresentationDeckRepository presentationDeckRepository,
            PresentationDeckMapper presentationDeckMapper
    ) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @Transactional
    public PresentationDeck get(UUID workspaceId, Long sprintId) {
        return presentationDeckRepository.findFirstByWorkspaceIdAndReferenceTypeAndReferenceIdOrderByUpdatedAtDesc(
                        workspaceId,
                        SprintReviewToPresentationDeckMapper.SPRINT_REFERENCE_TYPE,
                        sprintId.toString()
                )
                .map(presentationDeckMapper::toDomain)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, sprintId.toString()));
    }
}

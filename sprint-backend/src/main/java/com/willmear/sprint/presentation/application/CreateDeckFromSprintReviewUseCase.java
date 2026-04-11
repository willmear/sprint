package com.willmear.sprint.presentation.application;

import com.willmear.sprint.artifact.api.ArtifactService;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.export.mapper.ArtifactToSprintReviewMapper;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.mapper.SprintReviewToPresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateDeckFromSprintReviewUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;
    private final ArtifactService artifactService;
    private final ArtifactToSprintReviewMapper artifactToSprintReviewMapper;
    private final SprintReviewToPresentationDeckMapper sprintReviewToPresentationDeckMapper;

    public CreateDeckFromSprintReviewUseCase(
            PresentationDeckRepository presentationDeckRepository,
            PresentationDeckMapper presentationDeckMapper,
            ArtifactService artifactService,
            ArtifactToSprintReviewMapper artifactToSprintReviewMapper,
            SprintReviewToPresentationDeckMapper sprintReviewToPresentationDeckMapper
    ) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
        this.artifactService = artifactService;
        this.artifactToSprintReviewMapper = artifactToSprintReviewMapper;
        this.sprintReviewToPresentationDeckMapper = sprintReviewToPresentationDeckMapper;
    }

    @Transactional
    public PresentationDeck createOrGet(UUID workspaceId, Long sprintId) {
        return presentationDeckRepository
                .findFirstByWorkspaceIdAndReferenceTypeAndReferenceIdOrderByUpdatedAtDesc(
                        workspaceId,
                        SprintReviewToPresentationDeckMapper.SPRINT_REFERENCE_TYPE,
                        sprintId.toString()
                )
                .map(presentationDeckMapper::toDomain)
                .orElseGet(() -> createFromLatestReview(workspaceId, sprintId));
    }

    private PresentationDeck createFromLatestReview(UUID workspaceId, Long sprintId) {
        Artifact artifact = artifactService.getLatest(
                workspaceId,
                ArtifactType.SPRINT_REVIEW,
                SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE,
                sprintId.toString()
        );
        PresentationDeck initialDeck = sprintReviewToPresentationDeckMapper.toDeck(
                artifact,
                artifactToSprintReviewMapper.toSprintReview(artifact)
        );
        return presentationDeckMapper.toDomain(presentationDeckRepository.save(presentationDeckMapper.toEntity(initialDeck)));
    }
}

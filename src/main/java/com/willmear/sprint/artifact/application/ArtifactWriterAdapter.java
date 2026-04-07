package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.port.ArtifactWriterPort;
import org.springframework.stereotype.Component;

@Component
public class ArtifactWriterAdapter implements ArtifactWriterPort {

    private final SaveArtifactUseCase saveArtifactUseCase;
    private final SprintReviewArtifactMapper sprintReviewArtifactMapper;

    public ArtifactWriterAdapter(
            SaveArtifactUseCase saveArtifactUseCase,
            SprintReviewArtifactMapper sprintReviewArtifactMapper
    ) {
        this.saveArtifactUseCase = saveArtifactUseCase;
        this.sprintReviewArtifactMapper = sprintReviewArtifactMapper;
    }

    @Override
    public void write(SprintReview sprintReview) {
        saveArtifactUseCase.save(sprintReviewArtifactMapper.toArtifact(sprintReview));
    }
}

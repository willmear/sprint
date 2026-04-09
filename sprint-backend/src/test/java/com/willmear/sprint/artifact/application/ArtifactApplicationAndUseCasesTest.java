package com.willmear.sprint.artifact.application;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactStatus;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.entity.ArtifactEntity;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.common.exception.ArtifactNotFoundException;
import com.willmear.sprint.common.exception.ArtifactPersistenceException;
import com.willmear.sprint.common.exception.LatestArtifactNotFoundException;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArtifactApplicationAndUseCasesTest {

    private final ArtifactRepository artifactRepository = mock(ArtifactRepository.class);
    private final ArtifactMapper artifactMapper = mock(ArtifactMapper.class);

    @Test
    void shouldSaveArtifactThroughUseCase() {
        SaveArtifactUseCase useCase = new SaveArtifactUseCase(artifactRepository, artifactMapper);
        Artifact artifact = TestSprintReviewFactory.artifact();
        ArtifactEntity entity = new ArtifactEntity();
        entity.setId(artifact.id());
        when(artifactMapper.toEntity(artifact)).thenReturn(entity);
        when(artifactRepository.save(entity)).thenReturn(entity);
        when(artifactMapper.toDomain(entity)).thenReturn(artifact);

        assertThat(useCase.save(artifact)).isEqualTo(artifact);
    }

    @Test
    void shouldWrapSaveFailures() {
        SaveArtifactUseCase useCase = new SaveArtifactUseCase(artifactRepository, artifactMapper);
        Artifact artifact = TestSprintReviewFactory.artifact();
        when(artifactMapper.toEntity(artifact)).thenThrow(new IllegalStateException("boom"));

        assertThatThrownBy(() -> useCase.save(artifact))
                .isInstanceOf(ArtifactPersistenceException.class);
    }

    @Test
    void shouldGetAndListArtifacts() {
        GetArtifactUseCase getArtifactUseCase = new GetArtifactUseCase(artifactRepository, artifactMapper);
        GetLatestArtifactUseCase getLatestArtifactUseCase = new GetLatestArtifactUseCase(artifactRepository, artifactMapper);
        ListArtifactsUseCase listArtifactsUseCase = new ListArtifactsUseCase(artifactRepository, artifactMapper);
        Artifact artifact = TestSprintReviewFactory.artifact();
        ArtifactEntity entity = new ArtifactEntity();
        entity.setId(artifact.id());
        when(artifactRepository.findById(artifact.id())).thenReturn(Optional.of(entity));
        when(artifactRepository.findFirstByWorkspaceIdAndArtifactTypeAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
                artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId()
        )).thenReturn(Optional.of(entity));
        when(artifactRepository.findByWorkspaceIdAndArtifactTypeAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
                artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId()
        )).thenReturn(List.of(entity));
        when(artifactMapper.toDomain(entity)).thenReturn(artifact);

        assertThat(getArtifactUseCase.get(artifact.id())).isEqualTo(artifact);
        assertThat(getLatestArtifactUseCase.get(artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId()))
                .isEqualTo(artifact);
        assertThat(listArtifactsUseCase.list(artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId()))
                .containsExactly(artifact);
    }

    @Test
    void shouldThrowForMissingArtifactReads() {
        Artifact artifact = TestSprintReviewFactory.artifact();
        GetArtifactUseCase getArtifactUseCase = new GetArtifactUseCase(artifactRepository, artifactMapper);
        GetLatestArtifactUseCase getLatestArtifactUseCase = new GetLatestArtifactUseCase(artifactRepository, artifactMapper);
        when(artifactRepository.findById(artifact.id())).thenReturn(Optional.empty());
        when(artifactRepository.findFirstByWorkspaceIdAndArtifactTypeAndReferenceTypeAndReferenceIdOrderByGeneratedAtDescCreatedAtDesc(
                artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId()
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getArtifactUseCase.get(artifact.id())).isInstanceOf(ArtifactNotFoundException.class);
        assertThatThrownBy(() -> getLatestArtifactUseCase.get(artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId()))
                .isInstanceOf(LatestArtifactNotFoundException.class);
    }

    @Test
    void shouldDelegateThroughApplicationServiceAndWriterAdapter() {
        SaveArtifactUseCase saveArtifactUseCase = mock(SaveArtifactUseCase.class);
        GetArtifactUseCase getArtifactUseCase = mock(GetArtifactUseCase.class);
        GetLatestArtifactUseCase getLatestArtifactUseCase = mock(GetLatestArtifactUseCase.class);
        ListArtifactsUseCase listArtifactsUseCase = mock(ListArtifactsUseCase.class);
        ArtifactApplicationService service = new ArtifactApplicationService(
                saveArtifactUseCase,
                getArtifactUseCase,
                getLatestArtifactUseCase,
                listArtifactsUseCase
        );
        Artifact artifact = TestSprintReviewFactory.artifact();
        when(saveArtifactUseCase.save(artifact)).thenReturn(artifact);
        when(getArtifactUseCase.get(artifact.id())).thenReturn(artifact);
        when(getLatestArtifactUseCase.get(artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId())).thenReturn(artifact);
        when(listArtifactsUseCase.list(artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId())).thenReturn(List.of(artifact));

        assertThat(service.save(artifact)).isEqualTo(artifact);
        assertThat(service.get(artifact.id())).isEqualTo(artifact);
        assertThat(service.getLatest(artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId())).isEqualTo(artifact);
        assertThat(service.list(artifact.workspaceId(), artifact.artifactType(), artifact.referenceType(), artifact.referenceId())).containsExactly(artifact);

        SaveArtifactUseCase realSave = mock(SaveArtifactUseCase.class);
        SprintReviewArtifactMapper sprintReviewArtifactMapper = mock(SprintReviewArtifactMapper.class);
        ArtifactWriterAdapter writerAdapter = new ArtifactWriterAdapter(realSave, sprintReviewArtifactMapper);
        SprintReview review = TestSprintReviewFactory.review(UUID.randomUUID(), 44L, "DIRECT");
        when(sprintReviewArtifactMapper.toArtifact(review)).thenReturn(artifact);

        writerAdapter.write(review);

        verify(realSave).save(artifact);
    }
}

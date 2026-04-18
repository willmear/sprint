package com.willmear.sprint.artifact.application;

import com.willmear.sprint.artifact.api.ArtifactService;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.common.exception.ResourceAccessDeniedException;
import com.willmear.sprint.common.exception.WorkspaceAccessDeniedException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ArtifactApplicationService implements ArtifactService {

    private final SaveArtifactUseCase saveArtifactUseCase;
    private final GetArtifactUseCase getArtifactUseCase;
    private final GetLatestArtifactUseCase getLatestArtifactUseCase;
    private final ListArtifactsUseCase listArtifactsUseCase;

    public ArtifactApplicationService(
            SaveArtifactUseCase saveArtifactUseCase,
            GetArtifactUseCase getArtifactUseCase,
            GetLatestArtifactUseCase getLatestArtifactUseCase,
            ListArtifactsUseCase listArtifactsUseCase
    ) {
        this.saveArtifactUseCase = saveArtifactUseCase;
        this.getArtifactUseCase = getArtifactUseCase;
        this.getLatestArtifactUseCase = getLatestArtifactUseCase;
        this.listArtifactsUseCase = listArtifactsUseCase;
    }

    @Override
    public Artifact save(Artifact artifact) {
        return saveArtifactUseCase.save(artifact);
    }

    @Override
    public Artifact get(UUID artifactId) {
        try {
            return getArtifactUseCase.get(artifactId);
        } catch (WorkspaceAccessDeniedException exception) {
            throw new ResourceAccessDeniedException("artifact", artifactId, exception);
        }
    }

    @Override
    public Artifact getLatest(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId) {
        return getLatestArtifactUseCase.get(workspaceId, artifactType, referenceType, referenceId);
    }

    @Override
    public List<Artifact> list(UUID workspaceId, ArtifactType artifactType, String referenceType, String referenceId) {
        return listArtifactsUseCase.list(workspaceId, artifactType, referenceType, referenceId);
    }
}

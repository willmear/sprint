package com.willmear.sprint.mapper;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.willmear.sprint.api.response.CreateJobResponse;
import com.willmear.sprint.api.response.JobResponse;
import com.willmear.sprint.api.response.WorkspaceResponse;
import com.willmear.sprint.artifact.domain.Artifact;
import com.willmear.sprint.artifact.domain.ArtifactStatus;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.entity.ArtifactEntity;
import com.willmear.sprint.artifact.mapper.ArtifactMapper;
import com.willmear.sprint.artifact.mapper.ArtifactResponseMapper;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.jobs.entity.JobEntity;
import com.willmear.sprint.jobs.mapper.JobMapper;
import com.willmear.sprint.jobs.mapper.JobResponseMapper;
import com.willmear.sprint.workspace.domain.model.Workspace;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import com.willmear.sprint.workspace.mapper.WorkspaceMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapperSmokeTest {

    @Test
    void shouldMapWorkspaceDomainAndResponses() {
        WorkspaceMapper mapper = new WorkspaceMapper();
        WorkspaceEntity entity = new WorkspaceEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("SprintIQ");
        entity.setDescription("Desc");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        Workspace workspace = mapper.toDomain(entity);
        WorkspaceResponse response = mapper.toResponse(workspace);

        assertThat(workspace.name()).isEqualTo("SprintIQ");
        assertThat(response.name()).isEqualTo("SprintIQ");
        assertThat(mapper.toSummaryResponse(workspace).name()).isEqualTo("SprintIQ");
    }

    @Test
    void shouldMapArtifactsDomainEntityAndResponses() {
        ArtifactMapper mapper = new ArtifactMapper();
        ArtifactResponseMapper responseMapper = new ArtifactResponseMapper();
        Instant now = Instant.now();
        Artifact artifact = new Artifact(UUID.randomUUID(), UUID.randomUUID(), ArtifactType.SPRINT_REVIEW, ArtifactStatus.GENERATED,
                "SPRINT", "77", JsonNodeFactory.instance.objectNode(), "# md", "title", "summary", "AI", "v1", now, now, now);

        ArtifactEntity entity = mapper.toEntity(artifact);
        Artifact mapped = mapper.toDomain(entity);

        assertThat(mapped.referenceId()).isEqualTo("77");
        assertThat(responseMapper.toResponse(mapped).artifactType()).isEqualTo("SPRINT_REVIEW");
        assertThat(responseMapper.toSummaryResponse(mapped).title()).isEqualTo("title");
    }

    @Test
    void shouldMapJobsDomainEntityAndResponses() {
        JobMapper mapper = new JobMapper();
        JobResponseMapper responseMapper = new JobResponseMapper();
        Instant now = Instant.now();
        Job job = new Job(UUID.randomUUID(), UUID.randomUUID(), JobType.SYNC_SPRINT, JobStatus.PENDING,
                "default", JsonNodeFactory.instance.objectNode(), 0, 3, now, null, null, null, null, null, null, null, now, now);

        JobEntity entity = mapper.toEntity(job);
        Job mapped = mapper.toDomain(entity);
        JobResponse response = responseMapper.toResponse(mapped);
        CreateJobResponse createResponse = responseMapper.toCreateResponse(mapped);

        assertThat(mapped.jobType()).isEqualTo(JobType.SYNC_SPRINT);
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(createResponse.status()).isEqualTo("PENDING");
    }
}

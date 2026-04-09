package com.willmear.sprint.jobs.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.jobs.entity.JobEntity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public Job toDomain(JobEntity entity) {
        return new Job(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getJobType(),
                entity.getStatus(),
                entity.getQueueName(),
                entity.getPayload(),
                entity.getAttemptCount(),
                entity.getMaxAttempts(),
                entity.getAvailableAt(),
                entity.getLockedAt(),
                entity.getLockedBy(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getFailedAt(),
                entity.getErrorMessage(),
                entity.getErrorCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public JobEntity toEntity(Job job) {
        JobEntity entity = new JobEntity();
        entity.setId(job.id());
        entity.setWorkspaceId(job.workspaceId());
        entity.setJobType(job.jobType());
        entity.setStatus(job.status());
        entity.setQueueName(job.queueName());
        entity.setPayload(job.payload());
        entity.setAttemptCount(job.attemptCount());
        entity.setMaxAttempts(job.maxAttempts());
        entity.setAvailableAt(job.availableAt());
        entity.setLockedAt(job.lockedAt());
        entity.setLockedBy(job.lockedBy());
        entity.setStartedAt(job.startedAt());
        entity.setCompletedAt(job.completedAt());
        entity.setFailedAt(job.failedAt());
        entity.setErrorMessage(job.errorMessage());
        entity.setErrorCode(job.errorCode());
        entity.setCreatedAt(job.createdAt());
        entity.setUpdatedAt(job.updatedAt());
        return entity;
    }

    public Job newPendingJob(UUID workspaceId, JobType jobType, String queueName, JsonNode payload, int maxAttempts, Instant availableAt) {
        Instant now = Instant.now();
        return new Job(
                null,
                workspaceId,
                jobType,
                JobStatus.PENDING,
                queueName,
                payload != null ? payload : JsonNodeFactory.instance.objectNode(),
                0,
                maxAttempts,
                availableAt,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now
        );
    }
}

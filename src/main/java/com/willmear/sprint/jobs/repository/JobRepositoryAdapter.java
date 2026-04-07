package com.willmear.sprint.jobs.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobFilter;
import com.willmear.sprint.jobs.entity.JobEntity;
import com.willmear.sprint.jobs.mapper.JobMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class JobRepositoryAdapter {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public JobRepositoryAdapter(JobRepository jobRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    public Job save(Job job) {
        JobEntity saved = jobRepository.save(jobMapper.toEntity(job));
        return jobMapper.toDomain(saved);
    }

    public Optional<Job> findById(UUID jobId) {
        return jobRepository.findById(jobId).map(jobMapper::toDomain);
    }

    public List<Job> findByFilter(JobFilter filter) {
        Specification<JobEntity> specification = (root, query, cb) -> cb.conjunction();
        if (filter.workspaceId() != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("workspaceId"), filter.workspaceId()));
        }
        if (filter.status() != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), filter.status()));
        }
        if (filter.jobType() != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("jobType"), filter.jobType()));
        }
        return jobRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(jobMapper::toDomain)
                .toList();
    }

    public List<Job> claimNextJobs(Instant now, int maxJobs, String workerId) {
        return jobRepository.claimNextJobs(now, maxJobs, workerId).stream()
                .map(jobMapper::toDomain)
                .toList();
    }

    public Job create(UUID workspaceId, com.willmear.sprint.jobs.domain.JobType jobType, String queueName, JsonNode payload, int maxAttempts, Instant availableAt) {
        Job job = jobMapper.newPendingJob(workspaceId, jobType, queueName, payload, maxAttempts, availableAt);
        return save(job);
    }
}

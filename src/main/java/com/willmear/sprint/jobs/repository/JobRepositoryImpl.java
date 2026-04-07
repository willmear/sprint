package com.willmear.sprint.jobs.repository;

import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.entity.JobEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepositoryImpl implements JobRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<JobEntity> claimNextJobs(Instant now, int maxJobs, String workerId) {
        @SuppressWarnings("unchecked")
        List<UUID> ids = entityManager.createNativeQuery("""
                select id
                from job
                where status = 'PENDING'
                  and available_at <= :now
                order by available_at asc, created_at asc
                for update skip locked
                """)
                .setParameter("now", now)
                .setMaxResults(maxJobs)
                .getResultList();

        if (ids.isEmpty()) {
            return List.of();
        }

        List<JobEntity> jobs = entityManager.createQuery("""
                select job
                from JobEntity job
                where job.id in :ids
                order by job.createdAt asc
                """, JobEntity.class)
                .setParameter("ids", ids)
                .getResultList();

        for (JobEntity job : jobs) {
            job.setStatus(JobStatus.RUNNING);
            job.setLockedAt(now);
            job.setLockedBy(workerId);
            job.setStartedAt(now);
            job.setAttemptCount(job.getAttemptCount() + 1);
        }

        entityManager.flush();
        return jobs;
    }
}

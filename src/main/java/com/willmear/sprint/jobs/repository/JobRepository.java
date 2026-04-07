package com.willmear.sprint.jobs.repository;

import com.willmear.sprint.jobs.entity.JobEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository extends JpaRepository<JobEntity, UUID>, JpaSpecificationExecutor<JobEntity>, JobRepositoryCustom {
}

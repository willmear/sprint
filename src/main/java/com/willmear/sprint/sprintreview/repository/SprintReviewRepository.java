package com.willmear.sprint.sprintreview.repository;

import com.willmear.sprint.sprintreview.entity.SprintReviewEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintReviewRepository extends JpaRepository<SprintReviewEntity, UUID> {

    Optional<SprintReviewEntity> findBySprintId(UUID sprintId);
}


package com.willmear.sprint.presentation.repository;

import com.willmear.sprint.presentation.entity.PresentationSlideElementEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationSlideElementRepository extends JpaRepository<PresentationSlideElementEntity, UUID> {

    List<PresentationSlideElementEntity> findBySlide_IdOrderByElementOrderAsc(UUID slideId);

    void deleteBySlide_Id(UUID slideId);
}

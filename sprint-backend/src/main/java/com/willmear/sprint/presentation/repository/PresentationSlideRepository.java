package com.willmear.sprint.presentation.repository;

import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresentationSlideRepository extends JpaRepository<PresentationSlideEntity, UUID> {

    List<PresentationSlideEntity> findByDeck_IdOrderBySlideOrderAsc(UUID deckId);

    void deleteByDeck_Id(UUID deckId);
}

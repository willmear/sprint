package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.response.PresentationDeckResponse;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.entity.PresentationDeckEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PresentationDeckMapper {

    private final PresentationSlideMapper presentationSlideMapper;

    public PresentationDeckMapper(PresentationSlideMapper presentationSlideMapper) {
        this.presentationSlideMapper = presentationSlideMapper;
    }

    public PresentationDeck toDomain(PresentationDeckEntity entity) {
        return new PresentationDeck(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getReferenceType(),
                entity.getReferenceId(),
                entity.getTitle(),
                entity.getSubtitle(),
                entity.getStatus(),
                entity.getSlides().stream().map(presentationSlideMapper::toDomain).toList(),
                entity.getSourceArtifactId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PresentationDeckResponse toResponse(PresentationDeck deck) {
        return new PresentationDeckResponse(
                deck.id(),
                deck.workspaceId(),
                deck.referenceType(),
                deck.referenceId(),
                deck.title(),
                deck.subtitle(),
                deck.status().name(),
                deck.sourceArtifactId(),
                deck.slides().stream().map(presentationSlideMapper::toResponse).toList(),
                deck.createdAt(),
                deck.updatedAt()
        );
    }

    public PresentationDeckEntity toEntity(PresentationDeck deck) {
        PresentationDeckEntity entity = new PresentationDeckEntity();
        entity.setId(deck.id());
        entity.setWorkspaceId(deck.workspaceId());
        entity.setReferenceType(deck.referenceType());
        entity.setReferenceId(deck.referenceId());
        entity.setTitle(deck.title());
        entity.setSubtitle(deck.subtitle());
        entity.setStatus(deck.status());
        entity.setSourceArtifactId(deck.sourceArtifactId());
        entity.setCreatedAt(deck.createdAt());
        entity.setUpdatedAt(deck.updatedAt());
        List<PresentationSlideEntity> slides = new ArrayList<>();
        for (var slide : deck.slides()) {
            slides.add(presentationSlideMapper.toEntity(entity, slide));
        }
        entity.setSlides(slides);
        return entity;
    }

    public PresentationDeck toDomain(PresentationDeckEntity existing, UpdateDeckRequest request) {
        List<com.willmear.sprint.presentation.domain.PresentationSlide> slides = new ArrayList<>();
        for (int index = 0; index < request.slides().size(); index++) {
            slides.add(presentationSlideMapper.toDomain(existing.getId(), index, request.slides().get(index)));
        }
        return new PresentationDeck(
                existing.getId(),
                existing.getWorkspaceId(),
                existing.getReferenceType(),
                existing.getReferenceId(),
                request.title(),
                request.subtitle(),
                request.status(),
                slides,
                existing.getSourceArtifactId(),
                existing.getCreatedAt(),
                existing.getUpdatedAt()
        );
    }
}

package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.request.UpdateSlideRequest;
import com.willmear.sprint.presentation.api.response.PresentationSlideElementResponse;
import com.willmear.sprint.presentation.api.response.PresentationSlideResponse;
import com.willmear.sprint.presentation.domain.PresentationSlide;
import com.willmear.sprint.presentation.domain.PresentationSlideElement;
import com.willmear.sprint.presentation.entity.PresentationDeckEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideElementEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PresentationSlideMapper {

    private final PresentationSlideElementMapper presentationSlideElementMapper;

    public PresentationSlideMapper(PresentationSlideElementMapper presentationSlideElementMapper) {
        this.presentationSlideElementMapper = presentationSlideElementMapper;
    }

    public PresentationSlide toDomain(PresentationSlideEntity entity) {
        return new PresentationSlide(
                entity.getId(),
                entity.getDeck().getId(),
                entity.getSlideOrder(),
                entity.getSlideType(),
                entity.getTitle(),
                entity.getBulletPoints(),
                entity.getBodyText(),
                entity.getSpeakerNotes(),
                entity.getSectionLabel(),
                entity.getLayoutType(),
                entity.getElements().stream().map(presentationSlideElementMapper::toDomain).toList(),
                entity.isHidden(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PresentationSlideResponse toResponse(PresentationSlide slide) {
        return new PresentationSlideResponse(
                slide.id(),
                slide.deckId(),
                slide.slideOrder(),
                slide.slideType().name(),
                slide.title(),
                slide.bulletPoints(),
                slide.bodyText(),
                slide.speakerNotes(),
                slide.sectionLabel(),
                slide.layoutType().name(),
                slide.elements().stream().map(presentationSlideElementMapper::toResponse).toList(),
                slide.hidden(),
                slide.createdAt(),
                slide.updatedAt()
        );
    }

    public PresentationSlideEntity toEntity(PresentationDeckEntity deck, PresentationSlide slide) {
        PresentationSlideEntity entity = new PresentationSlideEntity();
        entity.setId(slide.id());
        entity.setDeck(deck);
        entity.setSlideOrder(slide.slideOrder());
        entity.setSlideType(slide.slideType());
        entity.setTitle(slide.title());
        entity.setBulletPoints(slide.bulletPoints() == null ? List.of() : slide.bulletPoints());
        entity.setBodyText(slide.bodyText());
        entity.setSpeakerNotes(slide.speakerNotes());
        entity.setSectionLabel(slide.sectionLabel());
        entity.setLayoutType(slide.layoutType());
        List<PresentationSlideElementEntity> elements = new ArrayList<>();
        for (PresentationSlideElement element : slide.elements()) {
            elements.add(presentationSlideElementMapper.toEntity(entity, element));
        }
        entity.setElements(elements);
        entity.setHidden(slide.hidden());
        entity.setCreatedAt(slide.createdAt());
        entity.setUpdatedAt(slide.updatedAt());
        return entity;
    }

    public PresentationSlide toDomain(UUID deckId, int slideOrder, UpdateDeckRequest.UpdateDeckSlideRequest request) {
        List<PresentationSlideElement> elements = new ArrayList<>();
        for (int index = 0; index < request.elements().size(); index++) {
            elements.add(presentationSlideElementMapper.toDomain(request.id(), index, request.elements().get(index)));
        }
        return new PresentationSlide(
                request.id(),
                deckId,
                slideOrder,
                request.slideType(),
                request.title(),
                request.bulletPoints() == null ? List.of() : request.bulletPoints(),
                request.bodyText(),
                request.speakerNotes(),
                request.sectionLabel(),
                request.layoutType(),
                elements,
                Boolean.TRUE.equals(request.hidden()),
                null,
                null
        );
    }

    public void apply(PresentationSlideEntity entity, UpdateSlideRequest request) {
        entity.setSlideType(request.slideType());
        entity.setTitle(request.title());
        entity.setBulletPoints(request.bulletPoints() == null ? List.of() : request.bulletPoints());
        entity.setBodyText(request.bodyText());
        entity.setSpeakerNotes(request.speakerNotes());
        entity.setSectionLabel(request.sectionLabel());
        entity.setLayoutType(request.layoutType());
        List<PresentationSlideElementEntity> elements = new ArrayList<>();
        for (int index = 0; index < request.elements().size(); index++) {
            elements.add(presentationSlideElementMapper.toEntity(entity, presentationSlideElementMapper.toDomain(entity.getId(), index, request.elements().get(index))));
        }
        entity.setElements(elements);
        entity.setHidden(Boolean.TRUE.equals(request.hidden()));
    }
}

package com.willmear.sprint.presentation.mapper;

import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.request.UpdateSlideRequest;
import com.willmear.sprint.presentation.api.response.PresentationSlideElementResponse;
import com.willmear.sprint.presentation.domain.PresentationSlideElement;
import com.willmear.sprint.presentation.entity.PresentationSlideElementEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PresentationSlideElementMapper {

    public PresentationSlideElement toDomain(PresentationSlideElementEntity entity) {
        return new PresentationSlideElement(
                entity.getId(),
                entity.getSlide().getId(),
                entity.getElementOrder(),
                entity.getElementType(),
                entity.getRole(),
                entity.getTextContent(),
                entity.getX(),
                entity.getY(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getFontFamily(),
                entity.getFontSize(),
                entity.isBold(),
                entity.isItalic(),
                entity.getTextAlignment(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public PresentationSlideElementResponse toResponse(PresentationSlideElement element) {
        return new PresentationSlideElementResponse(
                element.id(),
                element.slideId(),
                element.elementOrder(),
                element.elementType().name(),
                element.role().name(),
                element.textContent(),
                element.x(),
                element.y(),
                element.width(),
                element.height(),
                element.fontFamily(),
                element.fontSize(),
                element.bold(),
                element.italic(),
                element.textAlignment().name(),
                element.createdAt(),
                element.updatedAt()
        );
    }

    public PresentationSlideElementEntity toEntity(PresentationSlideEntity slide, PresentationSlideElement element) {
        PresentationSlideElementEntity entity = new PresentationSlideElementEntity();
        entity.setId(element.id());
        entity.setSlide(slide);
        entity.setElementOrder(element.elementOrder());
        entity.setElementType(element.elementType());
        entity.setRole(element.role());
        entity.setTextContent(element.textContent());
        entity.setX(element.x());
        entity.setY(element.y());
        entity.setWidth(element.width());
        entity.setHeight(element.height());
        entity.setFontFamily(element.fontFamily());
        entity.setFontSize(element.fontSize());
        entity.setBold(element.bold());
        entity.setItalic(element.italic());
        entity.setTextAlignment(element.textAlignment());
        entity.setCreatedAt(element.createdAt());
        entity.setUpdatedAt(element.updatedAt());
        return entity;
    }

    public PresentationSlideElement toDomain(UUID slideId, int elementOrder, UpdateDeckRequest.UpdateDeckSlideElementRequest request) {
        return new PresentationSlideElement(
                request.id(),
                slideId,
                elementOrder,
                request.elementType(),
                request.role(),
                request.textContent(),
                request.x(),
                request.y(),
                request.width(),
                request.height(),
                request.fontFamily(),
                request.fontSize(),
                Boolean.TRUE.equals(request.bold()),
                Boolean.TRUE.equals(request.italic()),
                request.textAlignment(),
                null,
                null
        );
    }

    public PresentationSlideElement toDomain(UUID slideId, int elementOrder, UpdateSlideRequest.UpdateSlideElementRequest request) {
        return new PresentationSlideElement(
                request.id(),
                slideId,
                elementOrder,
                request.elementType(),
                request.role(),
                request.textContent(),
                request.x(),
                request.y(),
                request.width(),
                request.height(),
                request.fontFamily(),
                request.fontSize(),
                Boolean.TRUE.equals(request.bold()),
                Boolean.TRUE.equals(request.italic()),
                request.textAlignment(),
                null,
                null
        );
    }
}

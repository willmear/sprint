package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.InvalidSlideOrderException;
import com.willmear.sprint.common.exception.PresentationDeckCreationException;
import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.entity.PresentationDeckEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideElementEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SaveDeckUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final com.willmear.sprint.presentation.mapper.PresentationDeckMapper presentationDeckMapper;

    public SaveDeckUseCase(
            PresentationDeckRepository presentationDeckRepository,
            com.willmear.sprint.presentation.mapper.PresentationDeckMapper presentationDeckMapper
    ) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @Transactional
    public PresentationDeck save(UUID workspaceId, UUID deckId, UpdateDeckRequest request) {
        validateSlides(request);
        var existing = presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
        try {
            apply(existing, request);
            return presentationDeckMapper.toDomain(presentationDeckRepository.save(existing));
        } catch (RuntimeException exception) {
            throw new PresentationDeckCreationException("Failed to save slide deck " + deckId + ".", exception);
        }
    }

    private void validateSlides(UpdateDeckRequest request) {
        if (request.slides().isEmpty()) {
            throw new InvalidSlideOrderException("A deck must contain at least one slide.");
        }
        HashSet<UUID> seenIds = new HashSet<>();
        request.slides().stream()
                .map(UpdateDeckRequest.UpdateDeckSlideRequest::id)
                .filter(java.util.Objects::nonNull)
                .forEach(id -> {
                    if (!seenIds.add(id)) {
                        throw new InvalidSlideOrderException("Slide ids must be unique within a deck save request.");
                    }
                });
    }

    private void apply(PresentationDeckEntity deck, UpdateDeckRequest request) {
        deck.setTitle(request.title());
        deck.setSubtitle(request.subtitle());
        if (request.themeId() != null && !request.themeId().isBlank()) {
            deck.setThemeId(request.themeId());
        }
        deck.setStatus(request.status());

        Map<UUID, PresentationSlideEntity> existingSlidesById = new HashMap<>();
        Map<Integer, PresentationSlideEntity> existingSlidesByOrder = new HashMap<>();
        for (PresentationSlideEntity slide : deck.getSlides()) {
            if (slide.getId() != null) {
                existingSlidesById.put(slide.getId(), slide);
            }
            if (slide.getSlideOrder() != null) {
                existingSlidesByOrder.put(slide.getSlideOrder(), slide);
            }
        }

        Set<UUID> requestedSlideIds = new HashSet<>();
        for (UpdateDeckRequest.UpdateDeckSlideRequest slideRequest : request.slides()) {
            if (slideRequest.id() != null) {
                requestedSlideIds.add(slideRequest.id());
            }
        }
        deck.getSlides().removeIf(slide ->
                (slide.getId() != null && !requestedSlideIds.contains(slide.getId()))
                        || shouldRemoveLegacySlideWithoutId(slide, request)
        );

        for (int index = 0; index < request.slides().size(); index++) {
            UpdateDeckRequest.UpdateDeckSlideRequest slideRequest = request.slides().get(index);
            PresentationSlideEntity slide = resolveSlide(deck, existingSlidesById, existingSlidesByOrder, slideRequest, index);
            applySlide(slide, slideRequest, index);
            if (!deck.getSlides().contains(slide)) {
                deck.getSlides().add(slide);
            }
        }

        deck.getSlides().sort(Comparator.comparing(PresentationSlideEntity::getSlideOrder));
    }

    private PresentationSlideEntity resolveSlide(
            PresentationDeckEntity deck,
            Map<UUID, PresentationSlideEntity> existingSlidesById,
            Map<Integer, PresentationSlideEntity> existingSlidesByOrder,
            UpdateDeckRequest.UpdateDeckSlideRequest request,
            int slideOrder
    ) {
        PresentationSlideEntity slide = request.id() == null ? existingSlidesByOrder.get(slideOrder) : existingSlidesById.get(request.id());
        if (slide == null) {
            slide = new PresentationSlideEntity();
            if (request.id() != null) {
                slide.setId(request.id());
            }
            slide.setDeck(deck);
        }
        return slide;
    }

    private void applySlide(PresentationSlideEntity slide, UpdateDeckRequest.UpdateDeckSlideRequest request, int slideOrder) {
        slide.setSlideOrder(slideOrder);
        slide.setSlideType(request.slideType());
        slide.setTitle(request.title());
        slide.setBulletPoints(request.bulletPoints() == null ? List.of() : new ArrayList<>(request.bulletPoints()));
        slide.setBodyText(request.bodyText());
        slide.setSpeakerNotes(request.speakerNotes());
        slide.setSectionLabel(request.sectionLabel());
        slide.setBackgroundColor(request.backgroundColor());
        slide.setBackgroundStyleType(request.backgroundStyleType());
        slide.setShowGrid(Boolean.TRUE.equals(request.showGrid()));
        slide.setLayoutType(request.layoutType());
        slide.setTemplateType(request.templateType());
        slide.setHidden(Boolean.TRUE.equals(request.hidden()));

        Map<UUID, PresentationSlideElementEntity> existingElementsById = new HashMap<>();
        Map<Integer, PresentationSlideElementEntity> existingElementsByOrder = new HashMap<>();
        for (PresentationSlideElementEntity element : slide.getElements()) {
            if (element.getId() != null) {
                existingElementsById.put(element.getId(), element);
            }
            if (element.getElementOrder() != null) {
                existingElementsByOrder.put(element.getElementOrder(), element);
            }
        }

        Set<UUID> requestedElementIds = new HashSet<>();
        for (UpdateDeckRequest.UpdateDeckSlideElementRequest elementRequest : request.elements()) {
            if (elementRequest.id() != null) {
                requestedElementIds.add(elementRequest.id());
            }
        }
        slide.getElements().removeIf(element ->
                (element.getId() != null && !requestedElementIds.contains(element.getId()))
                        || shouldRemoveLegacyElementWithoutId(element, request)
        );

        for (int index = 0; index < request.elements().size(); index++) {
            UpdateDeckRequest.UpdateDeckSlideElementRequest elementRequest = request.elements().get(index);
            PresentationSlideElementEntity element = resolveElement(slide, existingElementsById, existingElementsByOrder, elementRequest, index);
            applyElement(element, elementRequest, index);
            if (!slide.getElements().contains(element)) {
                slide.getElements().add(element);
            }
        }

        normalizeElementOrdering(slide);
    }

    private PresentationSlideElementEntity resolveElement(
            PresentationSlideEntity slide,
            Map<UUID, PresentationSlideElementEntity> existingElementsById,
            Map<Integer, PresentationSlideElementEntity> existingElementsByOrder,
            UpdateDeckRequest.UpdateDeckSlideElementRequest request,
            int elementOrder
    ) {
        PresentationSlideElementEntity element =
                request.id() == null ? existingElementsByOrder.get(elementOrder) : existingElementsById.get(request.id());
        if (element == null) {
            element = new PresentationSlideElementEntity();
            if (request.id() != null) {
                element.setId(request.id());
            }
            element.setSlide(slide);
        }
        return element;
    }

    private void applyElement(
            PresentationSlideElementEntity element,
            UpdateDeckRequest.UpdateDeckSlideElementRequest request,
            int elementOrder
    ) {
        element.setElementOrder(elementOrder);
        element.setElementType(request.elementType());
        element.setRole(request.role());
        element.setTextContent(request.textContent());
        element.setX(request.x());
        element.setY(request.y());
        element.setWidth(request.width());
        element.setHeight(request.height());
        element.setZIndex(request.zIndex() == null ? elementOrder : request.zIndex());
        element.setRotationDegrees(request.rotationDegrees());
        element.setFillColor(request.fillColor());
        element.setBorderColor(request.borderColor());
        element.setBorderWidth(request.borderWidth());
        element.setTextColor(request.textColor());
        element.setFontFamily(request.fontFamily());
        element.setFontSize(request.fontSize());
        element.setBold(Boolean.TRUE.equals(request.bold()));
        element.setItalic(Boolean.TRUE.equals(request.italic()));
        element.setUnderline(Boolean.TRUE.equals(request.underline()));
        element.setTextAlignment(request.textAlignment());
        element.setShapeType(request.shapeType());
        element.setHidden(Boolean.TRUE.equals(request.hidden()));
    }

    private boolean shouldRemoveLegacySlideWithoutId(PresentationSlideEntity slide, UpdateDeckRequest request) {
        if (slide.getId() != null || slide.getSlideOrder() == null) {
            return false;
        }
        int slideOrder = slide.getSlideOrder();
        if (slideOrder >= request.slides().size()) {
            return true;
        }
        return request.slides().get(slideOrder).id() != null;
    }

    private boolean shouldRemoveLegacyElementWithoutId(
            PresentationSlideElementEntity element,
            UpdateDeckRequest.UpdateDeckSlideRequest request
    ) {
        if (element.getId() != null || element.getElementOrder() == null) {
            return false;
        }
        int elementOrder = element.getElementOrder();
        if (elementOrder >= request.elements().size()) {
            return true;
        }
        return request.elements().get(elementOrder).id() != null;
    }

    private void normalizeElementOrdering(PresentationSlideEntity slide) {
        slide.getElements().sort(Comparator
                .comparing(PresentationSlideElementEntity::getZIndex, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(PresentationSlideElementEntity::getElementOrder, Comparator.nullsLast(Integer::compareTo)));
        for (int index = 0; index < slide.getElements().size(); index++) {
            PresentationSlideElementEntity element = slide.getElements().get(index);
            element.setElementOrder(index);
            element.setZIndex(index);
        }
    }
}

package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.presentation.api.request.AddSlideRequest;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideElementType;
import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.TextAlignment;
import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideElementEntity;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AddSlideUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;

    public AddSlideUseCase(PresentationDeckRepository presentationDeckRepository, PresentationDeckMapper presentationDeckMapper) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @Transactional
    public PresentationDeck add(UUID workspaceId, UUID deckId, AddSlideRequest request) {
        var deck = presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
        PresentationSlideEntity slide = new PresentationSlideEntity();
        slide.setDeck(deck);
        slide.setSlideOrder(deck.getSlides().size());
        slide.setSlideType(request.slideType());
        slide.setTitle(request.title() == null || request.title().isBlank() ? "New slide" : request.title());
        slide.setBulletPoints(List.of("Add your bullet points here."));
        slide.setBodyText(null);
        slide.setSpeakerNotes(null);
        slide.setSectionLabel(request.sectionLabel());
        slide.setLayoutType(request.layoutType() != null ? request.layoutType() : SlideLayoutType.TITLE_AND_BULLETS);
        slide.setTemplateType(null);
        slide.setHidden(false);
        slide.setElements(List.of(
                textElement(slide, 0, SlideElementRole.TITLE, "New slide", 96.0, 74.0, 1088.0, 72.0, 30, true, false, TextAlignment.LEFT),
                textElement(slide, 1, SlideElementRole.BODY_BULLETS, "Add your bullet points here.", 96.0, 188.0, 720.0, 240.0, 22, false, false, TextAlignment.LEFT)
        ));
        deck.getSlides().add(slide);
        return presentationDeckMapper.toDomain(presentationDeckRepository.save(deck));
    }

    private PresentationSlideElementEntity textElement(
            PresentationSlideEntity slide,
            int order,
            SlideElementRole role,
            String text,
            double x,
            double y,
            double width,
            double height,
            int fontSize,
            boolean bold,
            boolean italic,
            TextAlignment alignment
    ) {
        PresentationSlideElementEntity element = new PresentationSlideElementEntity();
        element.setSlide(slide);
        element.setElementOrder(order);
        element.setElementType(SlideElementType.TEXT_BOX);
        element.setRole(role);
        element.setTextContent(text);
        element.setX(x);
        element.setY(y);
        element.setWidth(width);
        element.setHeight(height);
        element.setZIndex(order);
        element.setFontFamily("Aptos");
        element.setFontSize(fontSize);
        element.setBold(bold);
        element.setItalic(italic);
        element.setUnderline(false);
        element.setTextAlignment(alignment);
        element.setHidden(false);
        return element;
    }
}

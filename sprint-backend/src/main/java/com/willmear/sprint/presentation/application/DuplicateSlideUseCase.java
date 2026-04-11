package com.willmear.sprint.presentation.application;

import com.willmear.sprint.common.exception.PresentationDeckNotFoundException;
import com.willmear.sprint.common.exception.PresentationSlideNotFoundException;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.entity.PresentationSlideEntity;
import com.willmear.sprint.presentation.entity.PresentationSlideElementEntity;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DuplicateSlideUseCase {

    private final PresentationDeckRepository presentationDeckRepository;
    private final PresentationDeckMapper presentationDeckMapper;

    public DuplicateSlideUseCase(PresentationDeckRepository presentationDeckRepository, PresentationDeckMapper presentationDeckMapper) {
        this.presentationDeckRepository = presentationDeckRepository;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @Transactional
    public PresentationDeck duplicate(UUID workspaceId, UUID deckId, UUID slideId) {
        var deck = presentationDeckRepository.findByIdAndWorkspaceId(deckId, workspaceId)
                .orElseThrow(() -> new PresentationDeckNotFoundException(workspaceId, deckId));
        int sourceIndex = -1;
        PresentationSlideEntity source = null;
        for (int index = 0; index < deck.getSlides().size(); index++) {
            if (deck.getSlides().get(index).getId().equals(slideId)) {
                sourceIndex = index;
                source = deck.getSlides().get(index);
                break;
            }
        }
        if (source == null) {
            throw new PresentationSlideNotFoundException(deckId, slideId);
        }
        int orderOffset = deck.getSlides().size() + 1;
        for (int index = sourceIndex + 1; index < deck.getSlides().size(); index++) {
            var slide = deck.getSlides().get(index);
            slide.setSlideOrder(slide.getSlideOrder() + orderOffset);
        }
        presentationDeckRepository.saveAndFlush(deck);

        PresentationSlideEntity duplicate = new PresentationSlideEntity();
        duplicate.setDeck(deck);
        duplicate.setSlideOrder(sourceIndex + 1);
        duplicate.setSlideType(source.getSlideType());
        duplicate.setTitle(source.getTitle() + " copy");
        duplicate.setBulletPoints(List.copyOf(source.getBulletPoints()));
        duplicate.setBodyText(source.getBodyText());
        duplicate.setSpeakerNotes(source.getSpeakerNotes());
        duplicate.setSectionLabel(source.getSectionLabel());
        duplicate.setLayoutType(source.getLayoutType());
        duplicate.setHidden(source.isHidden());
        duplicate.setElements(source.getElements().stream()
                .map(element -> duplicateElement(duplicate, element))
                .toList());
        deck.getSlides().add(sourceIndex + 1, duplicate);
        for (int index = sourceIndex + 2; index < deck.getSlides().size(); index++) {
            deck.getSlides().get(index).setSlideOrder(index);
        }
        return presentationDeckMapper.toDomain(presentationDeckRepository.save(deck));
    }

    private PresentationSlideElementEntity duplicateElement(PresentationSlideEntity slide, PresentationSlideElementEntity source) {
        PresentationSlideElementEntity duplicate = new PresentationSlideElementEntity();
        duplicate.setSlide(slide);
        duplicate.setElementOrder(source.getElementOrder());
        duplicate.setElementType(source.getElementType());
        duplicate.setRole(source.getRole());
        duplicate.setTextContent(source.getTextContent());
        duplicate.setX(source.getX());
        duplicate.setY(source.getY());
        duplicate.setWidth(source.getWidth());
        duplicate.setHeight(source.getHeight());
        duplicate.setFontFamily(source.getFontFamily());
        duplicate.setFontSize(source.getFontSize());
        duplicate.setBold(source.isBold());
        duplicate.setItalic(source.isItalic());
        duplicate.setTextAlignment(source.getTextAlignment());
        return duplicate;
    }
}

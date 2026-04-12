package com.willmear.sprint.export.mapper;

import com.willmear.sprint.export.domain.PowerPointDeckRenderModel;
import com.willmear.sprint.export.domain.PowerPointShapeElementRenderModel;
import com.willmear.sprint.export.domain.PowerPointSlideRenderModel;
import com.willmear.sprint.export.domain.PowerPointTextElementRenderModel;
import com.willmear.sprint.presentation.domain.PresentationDeck;
import com.willmear.sprint.presentation.domain.PresentationSlide;
import com.willmear.sprint.presentation.domain.PresentationSlideElement;
import com.willmear.sprint.presentation.domain.SlideElementType;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class PresentationDeckToPowerPointMapper {

    private static final Comparator<PresentationSlide> SLIDE_ORDER =
            Comparator.comparing(PresentationSlide::slideOrder, Comparator.nullsLast(Integer::compareTo));
    private static final Comparator<PresentationSlideElement> ELEMENT_ORDER =
            Comparator.comparing(PresentationSlideElement::zIndex, Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(PresentationSlideElement::elementOrder, Comparator.nullsLast(Integer::compareTo));

    public PowerPointDeckRenderModel toRenderModel(PresentationDeck deck) {
        return new PowerPointDeckRenderModel(
                deck.title(),
                deck.subtitle(),
                deck.themeId(),
                deck.slides().stream()
                        .sorted(SLIDE_ORDER)
                        .map(this::toRenderModel)
                        .toList()
        );
    }

    private PowerPointSlideRenderModel toRenderModel(PresentationSlide slide) {
        return new PowerPointSlideRenderModel(
                slide.slideOrder(),
                slide.slideType(),
                slide.layoutType(),
                slide.templateType(),
                slide.title(),
                slide.backgroundColor(),
                slide.speakerNotes(),
                slide.hidden(),
                slide.elements().stream()
                        .sorted(ELEMENT_ORDER)
                        .filter(element -> !element.hidden())
                        .filter(element -> element.elementType() == SlideElementType.TEXT_BOX)
                        .map(this::toRenderModel)
                        .toList(),
                slide.elements().stream()
                        .sorted(ELEMENT_ORDER)
                        .filter(element -> !element.hidden())
                        .filter(element -> element.elementType() == SlideElementType.SHAPE)
                        .map(this::toShapeRenderModel)
                        .toList()
        );
    }

    private PowerPointTextElementRenderModel toRenderModel(PresentationSlideElement element) {
        return new PowerPointTextElementRenderModel(
                element.id() == null ? null : element.id().toString(),
                element.textContent(),
                defaultDouble(element.x(), 0.0),
                defaultDouble(element.y(), 0.0),
                defaultDouble(element.width(), 320.0),
                defaultDouble(element.height(), 80.0),
                element.zIndex() == null ? element.elementOrder() == null ? 0 : element.elementOrder() : element.zIndex(),
                element.textColor(),
                element.fontFamily() == null || element.fontFamily().isBlank() ? "Aptos" : element.fontFamily(),
                element.fontSize() == null ? 24 : element.fontSize(),
                element.bold(),
                element.italic(),
                element.underline(),
                element.textAlignment(),
                element.role()
        );
    }

    private PowerPointShapeElementRenderModel toShapeRenderModel(PresentationSlideElement element) {
        return new PowerPointShapeElementRenderModel(
                element.id() == null ? null : element.id().toString(),
                element.shapeType(),
                defaultDouble(element.x(), 0.0),
                defaultDouble(element.y(), 0.0),
                defaultDouble(element.width(), 160.0),
                defaultDouble(element.height(), 120.0),
                element.zIndex() == null ? element.elementOrder() == null ? 0 : element.elementOrder() : element.zIndex(),
                element.rotationDegrees() == null ? 0.0 : element.rotationDegrees(),
                element.fillColor(),
                element.borderColor(),
                element.borderWidth() == null ? 2 : element.borderWidth()
        );
    }

    private double defaultDouble(Double value, double fallback) {
        return value == null ? fallback : value;
    }
}

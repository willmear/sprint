package com.willmear.sprint.presentation.api.request;

import com.willmear.sprint.presentation.domain.DeckStatus;
import com.willmear.sprint.presentation.domain.BackgroundStyleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record UpdateDeckRequest(
        @NotBlank String title,
        String subtitle,
        String themeId,
        @NotNull DeckStatus status,
        @NotNull @Valid List<UpdateDeckSlideRequest> slides
) {

    public record UpdateDeckSlideRequest(
            UUID id,
            @NotNull com.willmear.sprint.presentation.domain.SlideType slideType,
            @NotBlank String title,
            List<String> bulletPoints,
            String bodyText,
            String speakerNotes,
            String sectionLabel,
            String backgroundColor,
            BackgroundStyleType backgroundStyleType,
            Boolean showGrid,
            @NotNull com.willmear.sprint.presentation.domain.SlideLayoutType layoutType,
            com.willmear.sprint.presentation.template.SlideTemplateType templateType,
            @NotNull @Valid List<UpdateDeckSlideElementRequest> elements,
            Boolean hidden
    ) {
    }

    public record UpdateDeckSlideElementRequest(
            UUID id,
            @NotNull com.willmear.sprint.presentation.domain.SlideElementType elementType,
            @NotNull com.willmear.sprint.presentation.domain.SlideElementRole role,
            String textContent,
            @NotNull Double x,
            @NotNull Double y,
            @NotNull Double width,
            @NotNull Double height,
            Integer zIndex,
            Double rotationDegrees,
            String fillColor,
            String borderColor,
            Integer borderWidth,
            String textColor,
            String fontFamily,
            Integer fontSize,
            Boolean bold,
            Boolean italic,
            Boolean underline,
            com.willmear.sprint.presentation.domain.TextAlignment textAlignment,
            com.willmear.sprint.presentation.domain.ShapeType shapeType,
            Boolean hidden
    ) {
    }
}

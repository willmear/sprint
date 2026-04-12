package com.willmear.sprint.presentation.api.request;

import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.BackgroundStyleType;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideElementType;
import com.willmear.sprint.presentation.domain.ShapeType;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.domain.TextAlignment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateSlideRequest(
        @NotNull SlideType slideType,
        @NotBlank String title,
        List<String> bulletPoints,
        String bodyText,
        String speakerNotes,
        String sectionLabel,
        String backgroundColor,
        BackgroundStyleType backgroundStyleType,
        Boolean showGrid,
        @NotNull SlideLayoutType layoutType,
        com.willmear.sprint.presentation.template.SlideTemplateType templateType,
        @NotNull @Valid List<UpdateSlideElementRequest> elements,
        Boolean hidden
) {
    public record UpdateSlideElementRequest(
            java.util.UUID id,
            @NotNull SlideElementType elementType,
            @NotNull SlideElementRole role,
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
            TextAlignment textAlignment,
            ShapeType shapeType,
            Boolean hidden
    ) {
    }
}

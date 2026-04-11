package com.willmear.sprint.presentation.api.request;

import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideElementType;
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
        @NotNull SlideLayoutType layoutType,
        @NotNull @Valid List<UpdateSlideElementRequest> elements,
        Boolean hidden
) {
    public record UpdateSlideElementRequest(
            java.util.UUID id,
            @NotNull SlideElementType elementType,
            @NotNull SlideElementRole role,
            @NotBlank String textContent,
            @NotNull Double x,
            @NotNull Double y,
            @NotNull Double width,
            @NotNull Double height,
            @NotBlank String fontFamily,
            @NotNull Integer fontSize,
            Boolean bold,
            Boolean italic,
            @NotNull TextAlignment textAlignment
    ) {
    }
}

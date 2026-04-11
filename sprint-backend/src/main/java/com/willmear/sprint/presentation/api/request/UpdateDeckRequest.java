package com.willmear.sprint.presentation.api.request;

import com.willmear.sprint.presentation.domain.DeckStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record UpdateDeckRequest(
        @NotBlank String title,
        String subtitle,
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
            @NotNull com.willmear.sprint.presentation.domain.SlideLayoutType layoutType,
            @NotNull @Valid List<UpdateDeckSlideElementRequest> elements,
            Boolean hidden
    ) {
    }

    public record UpdateDeckSlideElementRequest(
            UUID id,
            @NotNull com.willmear.sprint.presentation.domain.SlideElementType elementType,
            @NotNull com.willmear.sprint.presentation.domain.SlideElementRole role,
            @NotBlank String textContent,
            @NotNull Double x,
            @NotNull Double y,
            @NotNull Double width,
            @NotNull Double height,
            @NotBlank String fontFamily,
            @NotNull Integer fontSize,
            Boolean bold,
            Boolean italic,
            @NotNull com.willmear.sprint.presentation.domain.TextAlignment textAlignment
    ) {
    }
}

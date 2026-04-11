package com.willmear.sprint.presentation.api.request;

import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideType;
import jakarta.validation.constraints.NotNull;

public record AddSlideRequest(
        @NotNull SlideType slideType,
        String title,
        String sectionLabel,
        SlideLayoutType layoutType
) {
}

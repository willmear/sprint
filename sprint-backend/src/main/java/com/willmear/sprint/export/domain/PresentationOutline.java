package com.willmear.sprint.export.domain;

import java.util.List;

public record PresentationOutline(
        String title,
        List<PresentationSlide> slides
) {
}

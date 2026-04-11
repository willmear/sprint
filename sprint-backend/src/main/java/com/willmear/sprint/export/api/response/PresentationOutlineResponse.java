package com.willmear.sprint.export.api.response;

import java.util.List;

public record PresentationOutlineResponse(
        String title,
        List<PresentationSlideResponse> slides
) {
}

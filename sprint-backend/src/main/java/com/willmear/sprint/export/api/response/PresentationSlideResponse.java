package com.willmear.sprint.export.api.response;

import java.util.List;

public record PresentationSlideResponse(
        Integer slideNumber,
        String title,
        List<String> bulletPoints,
        String speakerNotes
) {
}

package com.willmear.sprint.export.domain;

import java.util.List;

public record PresentationSlide(
        Integer slideNumber,
        String title,
        List<String> bulletPoints,
        String speakerNotes
) {
}

package com.willmear.sprint.export.mapper;

import com.willmear.sprint.export.api.response.PresentationOutlineResponse;
import com.willmear.sprint.export.api.response.PresentationSlideResponse;
import com.willmear.sprint.export.domain.PresentationOutline;
import com.willmear.sprint.export.domain.PresentationSlide;
import org.springframework.stereotype.Component;

@Component
public class PresentationOutlineResponseMapper {

    public PresentationOutlineResponse toResponse(PresentationOutline presentationOutline) {
        return new PresentationOutlineResponse(
                presentationOutline.title(),
                presentationOutline.slides().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private PresentationSlideResponse toResponse(PresentationSlide slide) {
        return new PresentationSlideResponse(
                slide.slideNumber(),
                slide.title(),
                slide.bulletPoints(),
                slide.speakerNotes()
        );
    }
}

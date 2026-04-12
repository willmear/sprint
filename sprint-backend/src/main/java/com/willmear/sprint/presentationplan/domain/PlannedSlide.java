package com.willmear.sprint.presentationplan.domain;

import java.util.List;

public record PlannedSlide(
        Integer slideOrder,
        SlideIntent slideIntent,
        String title,
        String subtitle,
        List<SlideBlock> blocks,
        LayoutHint layoutHint,
        String speakerNotes
) {
}

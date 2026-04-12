package com.willmear.sprint.presentation.template;

import com.willmear.sprint.presentation.domain.SlideLayoutType;
import java.util.List;

public record SlideTemplate(
        SlideTemplateType type,
        SlideLayoutType layoutType,
        int maxBulletsPerSlide,
        List<SlideSlot> slots
) {

    public SlideSlot slot(String key) {
        return slots.stream()
                .filter(slot -> slot.key().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown template slot: " + key));
    }
}

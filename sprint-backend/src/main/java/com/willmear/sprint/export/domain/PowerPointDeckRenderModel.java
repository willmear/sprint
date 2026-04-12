package com.willmear.sprint.export.domain;

import java.util.List;

public record PowerPointDeckRenderModel(
        String title,
        String subtitle,
        String themeId,
        List<PowerPointSlideRenderModel> slides
) {
}

package com.willmear.sprint.export.domain;

import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.template.SlideTemplateType;
import java.util.List;

public record PowerPointSlideRenderModel(
        int slideOrder,
        SlideType slideType,
        SlideLayoutType layoutType,
        SlideTemplateType templateType,
        String title,
        String backgroundColor,
        String speakerNotes,
        boolean hidden,
        List<PowerPointTextElementRenderModel> textElements,
        List<PowerPointShapeElementRenderModel> shapeElements
) {
}

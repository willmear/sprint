package com.willmear.sprint.export.renderer;

import com.willmear.sprint.export.domain.PowerPointTextElementRenderModel;
import com.willmear.sprint.presentation.domain.SlideElementRole;
import com.willmear.sprint.presentation.domain.SlideType;
import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import org.springframework.stereotype.Component;

@Component
public class PowerPointTypographyMapper {

    private final PowerPointCoordinateMapper coordinateMapper;

    public PowerPointTypographyMapper(PowerPointCoordinateMapper coordinateMapper) {
        this.coordinateMapper = coordinateMapper;
    }

    public PowerPointTypography resolve(
            PresentationTheme theme,
            SlideType slideType,
            PowerPointTextElementRenderModel element
    ) {
        String fontFamily = element.role() == SlideElementRole.TITLE
                ? safe(theme.typography().titleFontFamily(), element.fontFamily())
                : safe(theme.typography().bodyFontFamily(), element.fontFamily());

        int baseFontSize = switch (element.role()) {
            case TITLE -> theme.typography().titleFontSize() + titleBoost(slideType);
            case SECTION_LABEL, FOOTER -> theme.typography().smallFontSize();
            case SUBTITLE, METRIC, FREEFORM -> theme.typography().subtitleFontSize() + subtitleAdjustment(slideType);
            case BODY, BODY_BULLETS, CALLOUT -> theme.typography().bodyFontSize() + bodyAdjustment(slideType);
        };

        int resolved = Math.max(
                12,
                element.fontSize() > 0
                        ? Math.max(baseFontSize, element.fontSize())
                        : baseFontSize
        );
        return new PowerPointTypography(fontFamily, coordinateMapper.toPointFontSize(resolved));
    }

    private int titleBoost(SlideType slideType) {
        return switch (slideType) {
            case TITLE -> 6;
            case BLOCKERS -> 1;
            default -> 0;
        };
    }

    private int subtitleAdjustment(SlideType slideType) {
        return slideType == SlideType.TITLE ? 2 : 0;
    }

    private int bodyAdjustment(SlideType slideType) {
        return switch (slideType) {
            case TITLE -> 1;
            case HIGHLIGHTS, BLOCKERS -> -1;
            default -> 0;
        };
    }

    private String safe(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback == null || fallback.isBlank() ? "Aptos" : fallback;
    }
}

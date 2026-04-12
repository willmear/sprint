package com.willmear.sprint.export.renderer;

import com.willmear.sprint.export.domain.PowerPointSlideRenderModel;
import com.willmear.sprint.presentation.template.SlideTemplateType;
import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import java.awt.Color;
import org.springframework.stereotype.Component;

@Component
public class PowerPointSlideStyleMapper {

    public PowerPointSlideStyle resolve(PresentationTheme theme, PowerPointSlideRenderModel slide) {
        Color background = parse(theme.colorPalette().surface());
        Color title = parse(theme.defaults().titleColor());
        Color subtitle = parse(theme.defaults().subtitleColor());
        Color body = parse(theme.defaults().bodyColor());
        Color accent = parse(theme.colorPalette().accent());
        Color accentSecondary = parse(theme.colorPalette().accentSecondary());
        SlideTemplateType templateType = slide.templateType() == null ? inferTemplateType(slide.slideType()) : slide.templateType();

        return switch (templateType) {
            case TITLE_SLIDE -> new PowerPointSlideStyle(
                    background,
                    title,
                    subtitle,
                    body,
                    accent,
                    accentSecondary,
                    true,
                    false,
                    false,
                    false,
                    12.0,
                    12.0,
                    18.0,
                    12.0,
                    4.0
            );
            case SECTION_DIVIDER, EXECUTIVE_SUMMARY, TWO_COLUMN_HIGHLIGHTS, CALLOUT_SUMMARY -> new PowerPointSlideStyle(
                    background,
                    title,
                    subtitle,
                    body,
                    templateType == SlideTemplateType.CALLOUT_SUMMARY ? accentSecondary : accent,
                    accentSecondary,
                    false,
                    true,
                    false,
                    templateType == SlideTemplateType.CALLOUT_SUMMARY,
                    10.0,
                    12.0,
                    18.0,
                    12.0,
                    5.0
            );
            case BLOCKERS_RISKS -> new PowerPointSlideStyle(
                    background,
                    title,
                    new Color(153, 27, 27),
                    body,
                    parse(theme.colorPalette().danger()),
                    accentSecondary,
                    false,
                    true,
                    false,
                    true,
                    10.0,
                    12.0,
                    20.0,
                    12.0,
                    5.0
            );
            case CLOSING_SUMMARY -> new PowerPointSlideStyle(
                    background,
                    title,
                    subtitle,
                    body,
                    accent,
                    accentSecondary,
                    false,
                    false,
                    true,
                    false,
                    10.0,
                    12.0,
                    18.0,
                    12.0,
                    4.0
            );
        };
    }

    private SlideTemplateType inferTemplateType(com.willmear.sprint.presentation.domain.SlideType slideType) {
        return switch (slideType) {
            case TITLE -> SlideTemplateType.TITLE_SLIDE;
            case OVERVIEW -> SlideTemplateType.EXECUTIVE_SUMMARY;
            case THEMES -> SlideTemplateType.TWO_COLUMN_HIGHLIGHTS;
            case HIGHLIGHTS -> SlideTemplateType.CALLOUT_SUMMARY;
            case BLOCKERS -> SlideTemplateType.BLOCKERS_RISKS;
            case SPEAKER_NOTES, CUSTOM -> SlideTemplateType.CLOSING_SUMMARY;
        };
    }

    private Color parse(String hex) {
        return Color.decode(hex);
    }
}

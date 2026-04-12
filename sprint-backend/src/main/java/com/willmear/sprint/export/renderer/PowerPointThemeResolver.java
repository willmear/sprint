package com.willmear.sprint.export.renderer;

import com.willmear.sprint.presentation.theme.application.PresentationThemeService;
import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import org.springframework.stereotype.Component;

@Component
public class PowerPointThemeResolver {

    private final PresentationThemeService presentationThemeService;

    public PowerPointThemeResolver(PresentationThemeService presentationThemeService) {
        this.presentationThemeService = presentationThemeService;
    }

    public PresentationTheme resolve(String themeId) {
        return presentationThemeService.resolveTheme(themeId);
    }
}

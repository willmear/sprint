package com.willmear.sprint.presentation.theme.application;

import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PresentationThemeApplicationService implements PresentationThemeService {

    private final ThemeRegistry themeRegistry;

    public PresentationThemeApplicationService(ThemeRegistry themeRegistry) {
        this.themeRegistry = themeRegistry;
    }

    @Override
    public PresentationTheme resolveTheme(String themeId) {
        return themeRegistry.resolve(themeId);
    }

    @Override
    public PresentationTheme defaultTheme() {
        return themeRegistry.defaultTheme();
    }

    @Override
    public List<PresentationTheme> listThemes() {
        return themeRegistry.list();
    }
}

package com.willmear.sprint.presentation.theme.application;

import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import java.util.List;

public interface PresentationThemeService {

    PresentationTheme resolveTheme(String themeId);

    PresentationTheme defaultTheme();

    List<PresentationTheme> listThemes();
}

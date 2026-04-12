package com.willmear.sprint.presentation.theme.registry;

import com.willmear.sprint.presentation.theme.domain.ColorPalette;
import com.willmear.sprint.presentation.theme.domain.PresentationTheme;
import com.willmear.sprint.presentation.theme.domain.SlideThemeStyle;
import com.willmear.sprint.presentation.theme.domain.SpacingScale;
import com.willmear.sprint.presentation.theme.domain.ThemeAccentStyle;
import com.willmear.sprint.presentation.theme.domain.TypographyScale;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ThemeRegistry {

    private final Map<String, PresentationTheme> themes;
    private final String defaultThemeId;

    public ThemeRegistry(@Value("${app.presentation.default-theme-id:corporate-clean}") String defaultThemeId) {
        this.defaultThemeId = defaultThemeId;
        LinkedHashMap<String, PresentationTheme> registeredThemes = new LinkedHashMap<>();
        register(registeredThemes, corporateClean());
        register(registeredThemes, modernMinimal());
        register(registeredThemes, executiveDarkAccent());
        register(registeredThemes, oceanBlueprint());
        register(registeredThemes, sunriseEnergy());
        register(registeredThemes, forestEditorial());
        register(registeredThemes, plumStrategy());
        register(registeredThemes, emberMomentum());
        register(registeredThemes, graphiteBoardroom());
        this.themes = Map.copyOf(registeredThemes);
    }

    public PresentationTheme resolve(String themeId) {
        if (themeId == null || themeId.isBlank()) {
            return defaultTheme();
        }
        return themes.getOrDefault(themeId, defaultTheme());
    }

    public PresentationTheme defaultTheme() {
        return themes.getOrDefault(defaultThemeId, themes.get("corporate-clean"));
    }

    public List<PresentationTheme> list() {
        return themes.values().stream().toList();
    }

    private void register(Map<String, PresentationTheme> themes, PresentationTheme theme) {
        themes.put(theme.themeId(), theme);
    }

    private PresentationTheme corporateClean() {
        return new PresentationTheme(
                "corporate-clean",
                "Corporate Clean",
                new ColorPalette("#F8FAFC", "#FFFFFF", "#0F172A", "#475569", "#2563EB", "#0EA5E9", "#DC2626", "#CBD5E1"),
                new TypographyScale("Aptos", "Aptos", 30, 19, 22, 14),
                new SpacingScale(84.0, 40.0, 28.0, 14.0),
                new SlideThemeStyle(
                        "#F8FAFC",
                        "#0F172A",
                        "#475569",
                        "#1E293B",
                        "#2563EB",
                        new ThemeAccentStyle("#2563EB", "#64748B", "#CBD5E1", "#EFF6FF")
                )
        );
    }

    private PresentationTheme modernMinimal() {
        return new PresentationTheme(
                "modern-minimal",
                "Modern Minimal",
                new ColorPalette("#FFFFFF", "#FFFFFF", "#111827", "#6B7280", "#111827", "#9CA3AF", "#DC2626", "#E5E7EB"),
                new TypographyScale("Aptos", "Aptos", 32, 18, 21, 13),
                new SpacingScale(92.0, 44.0, 24.0, 12.0),
                new SlideThemeStyle(
                        "#FFFFFF",
                        "#111827",
                        "#6B7280",
                        "#1F2937",
                        "#6B7280",
                        new ThemeAccentStyle("#6B7280", "#9CA3AF", "#E5E7EB", "#F9FAFB")
                )
        );
    }

    private PresentationTheme executiveDarkAccent() {
        return new PresentationTheme(
                "executive-dark-accent",
                "Executive Dark Accent",
                new ColorPalette("#0F172A", "#111827", "#F8FAFC", "#CBD5E1", "#38BDF8", "#A78BFA", "#F87171", "#334155"),
                new TypographyScale("Aptos", "Aptos", 30, 19, 21, 14),
                new SpacingScale(88.0, 40.0, 26.0, 12.0),
                new SlideThemeStyle(
                        "#0F172A",
                        "#F8FAFC",
                        "#CBD5E1",
                        "#E2E8F0",
                        "#38BDF8",
                        new ThemeAccentStyle("#38BDF8", "#94A3B8", "#334155", "#172554")
                )
        );
    }

    private PresentationTheme oceanBlueprint() {
        return new PresentationTheme(
                "ocean-blueprint",
                "Ocean Blueprint",
                new ColorPalette("#F3FAFF", "#FFFFFF", "#0C4A6E", "#0369A1", "#0284C7", "#22D3EE", "#DC2626", "#BAE6FD"),
                new TypographyScale("Aptos", "Calibri", 31, 19, 21, 14),
                new SpacingScale(88.0, 42.0, 26.0, 12.0),
                new SlideThemeStyle(
                        "#F3FAFF",
                        "#0C4A6E",
                        "#0369A1",
                        "#075985",
                        "#0284C7",
                        new ThemeAccentStyle("#0284C7", "#22D3EE", "#BAE6FD", "#E0F2FE")
                )
        );
    }

    private PresentationTheme sunriseEnergy() {
        return new PresentationTheme(
                "sunrise-energy",
                "Sunrise Energy",
                new ColorPalette("#FFF8F1", "#FFFFFF", "#7C2D12", "#C2410C", "#EA580C", "#FB923C", "#DC2626", "#FED7AA"),
                new TypographyScale("Trebuchet MS", "Arial", 32, 19, 21, 13),
                new SpacingScale(92.0, 44.0, 24.0, 12.0),
                new SlideThemeStyle(
                        "#FFF8F1",
                        "#7C2D12",
                        "#C2410C",
                        "#9A3412",
                        "#EA580C",
                        new ThemeAccentStyle("#EA580C", "#FB923C", "#FED7AA", "#FFF7ED")
                )
        );
    }

    private PresentationTheme forestEditorial() {
        return new PresentationTheme(
                "forest-editorial",
                "Forest Editorial",
                new ColorPalette("#F5FBF7", "#FFFFFF", "#14532D", "#3F6212", "#15803D", "#65A30D", "#B91C1C", "#CFE8D5"),
                new TypographyScale("Georgia", "Georgia", 31, 18, 20, 13),
                new SpacingScale(88.0, 42.0, 28.0, 12.0),
                new SlideThemeStyle(
                        "#F5FBF7",
                        "#14532D",
                        "#3F6212",
                        "#166534",
                        "#15803D",
                        new ThemeAccentStyle("#15803D", "#65A30D", "#CFE8D5", "#ECFDF3")
                )
        );
    }

    private PresentationTheme plumStrategy() {
        return new PresentationTheme(
                "plum-strategy",
                "Plum Strategy",
                new ColorPalette("#FCF7FF", "#FFFFFF", "#4A044E", "#7E22CE", "#A21CAF", "#C084FC", "#DC2626", "#E9D5FF"),
                new TypographyScale("Aptos", "Helvetica", 30, 19, 21, 13),
                new SpacingScale(86.0, 40.0, 26.0, 12.0),
                new SlideThemeStyle(
                        "#FCF7FF",
                        "#4A044E",
                        "#7E22CE",
                        "#6B21A8",
                        "#A21CAF",
                        new ThemeAccentStyle("#A21CAF", "#C084FC", "#E9D5FF", "#FAF5FF")
                )
        );
    }

    private PresentationTheme emberMomentum() {
        return new PresentationTheme(
                "ember-momentum",
                "Ember Momentum",
                new ColorPalette("#FFF5F5", "#FFFFFF", "#7F1D1D", "#B45309", "#DC2626", "#F59E0B", "#B91C1C", "#FECACA"),
                new TypographyScale("Verdana", "Verdana", 30, 18, 20, 13),
                new SpacingScale(84.0, 40.0, 24.0, 12.0),
                new SlideThemeStyle(
                        "#FFF5F5",
                        "#7F1D1D",
                        "#B45309",
                        "#991B1B",
                        "#DC2626",
                        new ThemeAccentStyle("#DC2626", "#F59E0B", "#FECACA", "#FEF2F2")
                )
        );
    }

    private PresentationTheme graphiteBoardroom() {
        return new PresentationTheme(
                "graphite-boardroom",
                "Graphite Boardroom",
                new ColorPalette("#111111", "#1C1C1C", "#F5F5F5", "#D4D4D8", "#A3E635", "#38BDF8", "#F87171", "#3F3F46"),
                new TypographyScale("Helvetica", "Arial", 30, 18, 20, 13),
                new SpacingScale(88.0, 40.0, 24.0, 12.0),
                new SlideThemeStyle(
                        "#111111",
                        "#F5F5F5",
                        "#D4D4D8",
                        "#E5E7EB",
                        "#A3E635",
                        new ThemeAccentStyle("#A3E635", "#38BDF8", "#3F3F46", "#1F2937")
                )
        );
    }
}

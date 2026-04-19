import type {
  PresentationSlide,
  PresentationSlideElement,
  PresentationThemeSummary,
  SlideTemplateType,
} from "@/types/presentation";

export type SlideChrome = {
  backgroundColor: string;
  titleColor: string;
  subtitleColor: string;
  bodyColor: string;
  accentColor: string;
  accentSecondaryColor: string;
  fullBleedHeaderAccent: boolean;
  leftAccentRail: boolean;
  fullBleedBottomAccent: boolean;
  emphasizeCallouts: boolean;
};

export type ElementPreviewStyle = {
  backgroundColor: string;
  borderColor: string;
  borderWidth: number;
  textColor: string;
  fontFamily: string;
  fontSize: number;
  borderRadius: string;
};

export function resolveSlideChrome(theme: PresentationThemeSummary, slide: PresentationSlide): SlideChrome {
  const templateType = inferTemplateType(slide);
  const backgroundColor = slide.backgroundColor || theme.colorPalette.surface || theme.colorPalette.background;
  const titleColor = theme.colorPalette.textPrimary;
  const subtitleColor = templateType === "BLOCKERS_RISKS" ? theme.colorPalette.danger : theme.colorPalette.textSecondary;
  const bodyColor = theme.colorPalette.textPrimary;
  const accentColor = templateType === "BLOCKERS_RISKS" ? theme.colorPalette.danger : theme.colorPalette.accent;
  const accentSecondaryColor = theme.colorPalette.accentSecondary;

  return {
    backgroundColor,
    titleColor,
    subtitleColor,
    bodyColor,
    accentColor,
    accentSecondaryColor,
    fullBleedHeaderAccent: templateType === "TITLE_SLIDE",
    leftAccentRail: ["SECTION_DIVIDER", "EXECUTIVE_SUMMARY", "TWO_COLUMN_HIGHLIGHTS", "CALLOUT_SUMMARY", "BLOCKERS_RISKS"].includes(templateType),
    fullBleedBottomAccent: templateType === "CLOSING_SUMMARY",
    emphasizeCallouts: templateType === "CALLOUT_SUMMARY" || templateType === "BLOCKERS_RISKS",
  };
}

export function resolveElementPreviewStyle(
  theme: PresentationThemeSummary,
  slide: PresentationSlide,
  element: PresentationSlideElement
): ElementPreviewStyle {
  const chrome = resolveSlideChrome(theme, slide);

  if (element.elementType === "SHAPE") {
    return {
      backgroundColor: element.fillColor || withAlpha(chrome.accentSecondaryColor, 0.22),
      borderColor: element.borderColor || chrome.accentColor,
      borderWidth: element.borderWidth ?? 2,
      textColor: element.textColor || chrome.bodyColor,
      fontFamily: element.fontFamily || theme.typography.bodyFontFamily,
      fontSize: element.fontSize || theme.typography.bodyFontSize,
      borderRadius: "4px",
    };
  }

  const isCallout = chrome.emphasizeCallouts && (element.role === "FREEFORM" || element.role === "CALLOUT");
  const isTitle = element.role === "TITLE";
  const isSubtitle = element.role === "SUBTITLE" || element.role === "SECTION_LABEL" || element.role === "FOOTER" || element.role === "FREEFORM";
  const isMetric = element.role === "METRIC";
  const isSection = element.role === "SECTION_LABEL";

  return {
    backgroundColor: isCallout ? withAlpha(chrome.accentSecondaryColor, 0.18) : (!element.fillColor || element.fillColor === "transparent" ? "transparent" : element.fillColor),
    borderColor: isCallout ? chrome.accentColor : (!element.borderColor || element.borderColor === "transparent" ? "transparent" : element.borderColor),
    borderWidth: isCallout ? Math.max(1.5, element.borderWidth ?? 1.5) : element.borderWidth ?? 0,
    textColor: element.textColor
      || (isSection ? chrome.accentColor : isTitle || isMetric || isCallout ? chrome.titleColor : isSubtitle ? chrome.subtitleColor : chrome.bodyColor),
    fontFamily: element.fontFamily || (isTitle ? theme.typography.titleFontFamily : theme.typography.bodyFontFamily),
    fontSize: element.fontSize
      || (isTitle
        ? theme.typography.titleFontSize
        : element.role === "SUBTITLE"
          ? theme.typography.subtitleFontSize
          : element.role === "FOOTER"
            ? theme.typography.smallFontSize
            : theme.typography.bodyFontSize),
    borderRadius: isCallout ? "18px" : "2px",
  };
}

export function inferTemplateType(slide: PresentationSlide): SlideTemplateType {
  if (slide.templateType) {
    return slide.templateType;
  }
  switch (slide.slideType) {
    case "TITLE":
      return "TITLE_SLIDE";
    case "OVERVIEW":
      return "EXECUTIVE_SUMMARY";
    case "THEMES":
      return "TWO_COLUMN_HIGHLIGHTS";
    case "HIGHLIGHTS":
      return "CALLOUT_SUMMARY";
    case "BLOCKERS":
      return "BLOCKERS_RISKS";
    case "SPEAKER_NOTES":
    case "CUSTOM":
    default:
      return "CLOSING_SUMMARY";
  }
}

export function withAlpha(hex: string, alpha: number) {
  const normalized = hex.replace("#", "");
  const full = normalized.length === 3 ? normalized.split("").map((part) => `${part}${part}`).join("") : normalized;
  const red = Number.parseInt(full.slice(0, 2), 16);
  const green = Number.parseInt(full.slice(2, 4), 16);
  const blue = Number.parseInt(full.slice(4, 6), 16);
  return `rgba(${red}, ${green}, ${blue}, ${alpha})`;
}

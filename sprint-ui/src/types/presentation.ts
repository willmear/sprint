export type DeckStatus = "DRAFT" | "READY" | "ARCHIVED";
export type SlideType = "TITLE" | "OVERVIEW" | "THEMES" | "HIGHLIGHTS" | "BLOCKERS" | "SPEAKER_NOTES" | "CUSTOM";
export type SlideLayoutType = "TITLE_ONLY" | "TITLE_AND_BULLETS" | "TITLE_BODY_NOTES" | "SECTION_SUMMARY";
export type SlideTemplateType =
  | "TITLE_SLIDE"
  | "SECTION_DIVIDER"
  | "EXECUTIVE_SUMMARY"
  | "TWO_COLUMN_HIGHLIGHTS"
  | "CALLOUT_SUMMARY"
  | "BLOCKERS_RISKS"
  | "CLOSING_SUMMARY";
export type SlideElementType = "TEXT_BOX" | "SHAPE";
export type SlideElementRole =
  | "TITLE"
  | "SUBTITLE"
  | "BODY"
  | "BODY_BULLETS"
  | "SECTION_LABEL"
  | "CALLOUT"
  | "METRIC"
  | "FOOTER"
  | "FREEFORM";
export type TextAlignment = "LEFT" | "CENTER" | "RIGHT";
export type ShapeType = "RECTANGLE" | "ROUNDED_RECTANGLE" | "ELLIPSE" | "CIRCLE" | "TRIANGLE" | "ARROW" | "LINE" | "DIAMOND";
export type BackgroundStyleType = "SOLID";

export interface ColorPalette {
  background: string;
  surface: string;
  textPrimary: string;
  textSecondary: string;
  accent: string;
  accentSecondary: string;
  danger: string;
  mutedBorder: string;
}

export interface TypographyScale {
  titleFontFamily: string;
  bodyFontFamily: string;
  titleFontSize: number;
  subtitleFontSize: number;
  bodyFontSize: number;
  smallFontSize: number;
}

export interface PresentationThemeSummary {
  themeId: string;
  displayName: string;
  colorPalette: ColorPalette;
  typography: TypographyScale;
}

export interface PresentationSlideElement {
  id: string;
  slideId: string;
  elementOrder: number;
  elementType: SlideElementType;
  role: SlideElementRole;
  textContent: string;
  x: number;
  y: number;
  width: number;
  height: number;
  zIndex?: number | null;
  rotationDegrees?: number | null;
  fillColor?: string | null;
  borderColor?: string | null;
  borderWidth?: number | null;
  textColor?: string | null;
  fontFamily: string;
  fontSize: number;
  bold: boolean;
  italic: boolean;
  underline?: boolean;
  textAlignment: TextAlignment;
  shapeType?: ShapeType | null;
  hidden?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PresentationSlide {
  id: string;
  deckId: string;
  slideOrder: number;
  slideType: SlideType;
  title: string;
  bulletPoints: string[];
  bodyText?: string | null;
  speakerNotes?: string | null;
  sectionLabel?: string | null;
  backgroundColor?: string | null;
  backgroundStyleType?: BackgroundStyleType | null;
  showGrid?: boolean;
  layoutType: SlideLayoutType;
  templateType?: SlideTemplateType | null;
  elements: PresentationSlideElement[];
  hidden: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PresentationDeck {
  id: string;
  workspaceId: string;
  referenceType: string;
  referenceId: string;
  title: string;
  subtitle?: string | null;
  themeId?: string | null;
  themeDisplayName?: string | null;
  theme?: PresentationThemeSummary | null;
  status: DeckStatus;
  sourceArtifactId?: string | null;
  slides: PresentationSlide[];
  createdAt: string;
  updatedAt: string;
}

export interface UpdateDeckRequest {
  title: string;
  subtitle?: string | null;
  themeId?: string | null;
  status: DeckStatus;
  slides: Array<{
    id?: string | null;
    slideType: SlideType;
    title: string;
    bulletPoints: string[];
    bodyText?: string | null;
    speakerNotes?: string | null;
    sectionLabel?: string | null;
    backgroundColor?: string | null;
    backgroundStyleType?: BackgroundStyleType | null;
    showGrid?: boolean | null;
    layoutType: SlideLayoutType;
    templateType?: SlideTemplateType | null;
    elements: Array<{
      id?: string | null;
      elementType: SlideElementType;
      role: SlideElementRole;
      textContent?: string | null;
      x: number;
      y: number;
      width: number;
      height: number;
      zIndex?: number | null;
      rotationDegrees?: number | null;
      fillColor?: string | null;
      borderColor?: string | null;
      borderWidth?: number | null;
      textColor?: string | null;
      fontFamily?: string | null;
      fontSize?: number | null;
      bold?: boolean;
      italic?: boolean;
      underline?: boolean;
      textAlignment?: TextAlignment | null;
      shapeType?: ShapeType | null;
      hidden?: boolean;
    }>;
    hidden?: boolean;
  }>;
}

export interface UpdateSlideRequest {
  slideType: SlideType;
  title: string;
  bulletPoints: string[];
  bodyText?: string | null;
  speakerNotes?: string | null;
  sectionLabel?: string | null;
  backgroundColor?: string | null;
  backgroundStyleType?: BackgroundStyleType | null;
  showGrid?: boolean | null;
  layoutType: SlideLayoutType;
  templateType?: SlideTemplateType | null;
  elements: Array<{
    id?: string | null;
    elementType: SlideElementType;
    role: SlideElementRole;
    textContent?: string | null;
    x: number;
    y: number;
    width: number;
    height: number;
    zIndex?: number | null;
    rotationDegrees?: number | null;
    fillColor?: string | null;
    borderColor?: string | null;
    borderWidth?: number | null;
    textColor?: string | null;
    fontFamily?: string | null;
    fontSize?: number | null;
    bold?: boolean;
    italic?: boolean;
    underline?: boolean;
    textAlignment?: TextAlignment | null;
    shapeType?: ShapeType | null;
    hidden?: boolean;
  }>;
  hidden?: boolean;
}

export interface ReorderSlidesRequest {
  slideIds: string[];
}

export interface AddSlideRequest {
  slideType: SlideType;
  title?: string | null;
  sectionLabel?: string | null;
  layoutType?: SlideLayoutType | null;
}

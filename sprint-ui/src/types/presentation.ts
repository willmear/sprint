export type DeckStatus = "DRAFT" | "READY" | "ARCHIVED";
export type SlideType = "TITLE" | "OVERVIEW" | "THEMES" | "HIGHLIGHTS" | "BLOCKERS" | "SPEAKER_NOTES" | "CUSTOM";
export type SlideLayoutType = "TITLE_ONLY" | "TITLE_AND_BULLETS" | "TITLE_BODY_NOTES" | "SECTION_SUMMARY";
export type SlideElementType = "TEXT_BOX";
export type SlideElementRole = "TITLE" | "BODY" | "SECTION_LABEL" | "FREEFORM";
export type TextAlignment = "LEFT" | "CENTER" | "RIGHT";

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
  fontFamily: string;
  fontSize: number;
  bold: boolean;
  italic: boolean;
  textAlignment: TextAlignment;
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
  layoutType: SlideLayoutType;
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
  status: DeckStatus;
  sourceArtifactId?: string | null;
  slides: PresentationSlide[];
  createdAt: string;
  updatedAt: string;
}

export interface UpdateDeckRequest {
  title: string;
  subtitle?: string | null;
  status: DeckStatus;
  slides: Array<{
    id?: string | null;
    slideType: SlideType;
    title: string;
    bulletPoints: string[];
    bodyText?: string | null;
    speakerNotes?: string | null;
    sectionLabel?: string | null;
    layoutType: SlideLayoutType;
    elements: Array<{
      id?: string | null;
      elementType: SlideElementType;
      role: SlideElementRole;
      textContent: string;
      x: number;
      y: number;
      width: number;
      height: number;
      fontFamily: string;
      fontSize: number;
      bold?: boolean;
      italic?: boolean;
      textAlignment: TextAlignment;
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
  layoutType: SlideLayoutType;
  elements: Array<{
    id?: string | null;
    elementType: SlideElementType;
    role: SlideElementRole;
    textContent: string;
    x: number;
    y: number;
    width: number;
    height: number;
    fontFamily: string;
    fontSize: number;
    bold?: boolean;
    italic?: boolean;
    textAlignment: TextAlignment;
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

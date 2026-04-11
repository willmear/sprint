export type ExportFormat = "MARKDOWN" | "PRESENTATION_OUTLINE" | "SPEAKER_NOTES" | "POWERPOINT";

export interface PresentationSlide {
  slideNumber: number;
  title: string;
  bulletPoints: string[];
  speakerNotes?: string | null;
}

export interface PresentationOutline {
  title: string;
  slides: PresentationSlide[];
}

export interface ExportResponse {
  format: ExportFormat;
  fileName: string;
  contentType: string;
  textContent?: string | null;
  structuredContent?: PresentationOutline | Record<string, unknown> | null;
  generatedAt: string;
}

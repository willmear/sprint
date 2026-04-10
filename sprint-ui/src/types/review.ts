export interface SprintReviewSummary {
  title: string;
  overview: string;
  deliverySummary: string;
  qualitySummary: string;
  outcomeSummary: string;
}

export interface SprintTheme {
  name: string;
  description: string;
  relatedIssueKeys: string[];
}

export interface SprintHighlight {
  title: string;
  description: string;
  relatedIssueKeys: string[];
  category: string;
}

export interface SprintBlocker {
  title: string;
  description: string;
  relatedIssueKeys: string[];
  severity: string;
}

export interface SpeakerNote {
  section: string;
  note: string;
  displayOrder: number;
}

export interface SprintReview {
  id: string;
  workspaceId: string;
  externalSprintId: number;
  sprintName: string;
  summary: SprintReviewSummary;
  themes: SprintTheme[];
  highlights: SprintHighlight[];
  blockers: SprintBlocker[];
  speakerNotes: SpeakerNote[];
  generatedAt: string;
  generationSource: string;
  status: string;
}

export interface GenerateSprintReviewRequest {
  includeComments?: boolean;
  includeChangelog?: boolean;
  forceRegenerate?: boolean;
  audience?: string;
  tone?: string;
}

export interface GenerateSprintReviewJobResponse {
  jobId: string;
  status: string;
  availableAt: string;
  createdAt: string;
}

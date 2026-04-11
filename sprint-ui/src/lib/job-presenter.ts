import type { Job, JobSummary } from "@/types/job";

type JobLike = Job | JobSummary;

export type JobPresentation = {
  label: string;
  category: string;
  description: string;
  completionLabel?: string;
};

type ReviewJobPayload = {
  workspaceId?: string;
  externalSprintId?: number;
  sprintId?: number | string;
  referenceId?: number | string;
};

export function getJobPresentation(jobType: string): JobPresentation {
  switch (jobType) {
    case "GENERATE_SPRINT_REVIEW":
      return {
        label: "Sprint review generation",
        category: "Review jobs",
        description: "Creates the persisted sprint review artifact that users read and share.",
        completionLabel: "Open review",
      };
    case "SYNC_SPRINT":
      return {
        label: "Sprint sync",
        category: "Data sync jobs",
        description: "Refreshes sprint issues, comments, and changelog data from Jira.",
      };
    case "INDEX_SPRINT_DOCUMENTS":
      return {
        label: "Sprint context indexing",
        category: "Data sync jobs",
        description: "Prepares sprint documents and retrieval context used by downstream generation jobs.",
      };
    case "GENERATE_SLIDE_DECK":
      return {
        label: "Slide deck generation",
        category: "Presentation jobs",
        description: "Builds a presentation artifact from an existing sprint review.",
      };
    default:
      return {
        label: jobType.replaceAll("_", " ").toLowerCase().replace(/\b\w/g, (value) => value.toUpperCase()),
        category: "Other jobs",
        description: "Background work running in the shared jobs system.",
      };
  }
}

export function isCompletedReviewJob(job: JobLike) {
  return job.jobType === "GENERATE_SPRINT_REVIEW" && job.status === "COMPLETED";
}

export function getReviewHref(job: Job) {
  const payload = job.payload as ReviewJobPayload | null;
  const workspaceId = job.workspaceId || payload?.workspaceId;
  const sprintId = resolveSprintId(payload);
  if (!workspaceId || sprintId == null) {
    return null;
  }
  return `/review/${workspaceId}/${sprintId}`;
}

function resolveSprintId(payload: ReviewJobPayload | null | undefined) {
  if (!payload) {
    return null;
  }
  const candidates = [payload.externalSprintId, payload.sprintId, payload.referenceId];
  for (const candidate of candidates) {
    if (typeof candidate === "number" && Number.isFinite(candidate)) {
      return candidate;
    }
    if (typeof candidate === "string" && candidate.trim() !== "" && /^\d+$/.test(candidate.trim())) {
      return candidate.trim();
    }
  }
  return null;
}

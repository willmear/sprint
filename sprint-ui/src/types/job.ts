export interface JobSummary {
  id: string;
  workspaceId: string;
  jobType: string;
  status: string;
  attemptCount: number;
  maxAttempts: number;
  availableAt?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Job {
  id: string;
  workspaceId: string;
  jobType: string;
  status: string;
  queueName: string;
  payload: unknown;
  attemptCount: number;
  maxAttempts: number;
  availableAt?: string | null;
  lockedAt?: string | null;
  lockedBy?: string | null;
  startedAt?: string | null;
  completedAt?: string | null;
  failedAt?: string | null;
  errorMessage?: string | null;
  errorCode?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateJobResponse {
  id: string;
  status: string;
  availableAt: string;
  createdAt: string;
}

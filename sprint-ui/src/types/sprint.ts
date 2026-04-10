export interface IssueComment {
  authorDisplayName: string;
  body: string;
  createdAtExternal?: string | null;
}

export interface Issue {
  issueKey: string;
  externalIssueId?: string | null;
  summary: string;
  description?: string | null;
  issueType: string;
  status: string;
  priority?: string | null;
  assigneeDisplayName?: string | null;
  reporterDisplayName?: string | null;
  storyPoints?: number | null;
  createdAtExternal?: string | null;
  updatedAtExternal?: string | null;
}

export interface Sprint {
  sprintId: number;
  name: string;
  state: string;
  goal?: string | null;
  boardId?: number | null;
  issueCount?: number | null;
  startDate?: string | null;
  endDate?: string | null;
  completeDate?: string | null;
  syncedAt?: string | null;
}

export interface AvailableJiraSprint {
  sprintId: number;
  sprintName: string;
  state: string;
  boardId?: number | null;
  boardName?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  completeDate?: string | null;
}

export interface SyncSprintResponse {
  workspaceId: string;
  jiraConnectionId: string;
  sprintId: number;
  sprintName: string;
  issueCount: number;
  commentCount: number;
  changelogEventCount: number;
  syncedAt: string;
  status: string;
  message: string;
}

export interface IssueSummary {
  issueKey: string;
  summary: string;
  description?: string | null;
  issueType: string;
  status: string;
  priority?: string | null;
  assigneeDisplayName?: string | null;
  storyPoints?: number | null;
  bugFix: boolean;
  technicalWork: boolean;
  comments: IssueComment[];
}

export interface SprintContext {
  workspaceId: string;
  jiraConnectionId?: string | null;
  externalSprintId: number;
  sprintName: string;
  sprintGoal?: string | null;
  sprintState: string;
  sprintStartDate?: string | null;
  sprintEndDate?: string | null;
  completedIssues: IssueSummary[];
  inProgressIssues: IssueSummary[];
  carriedOverIssues: IssueSummary[];
  bugFixes: IssueSummary[];
  technicalImprovements: IssueSummary[];
  allIssues: IssueSummary[];
  notableComments: string[];
  blockers: string[];
  totalIssueCount?: number | null;
  totalCommentCount?: number | null;
  totalChangelogCount?: number | null;
  assembledAt: string;
}

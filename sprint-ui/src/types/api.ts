export interface ApiErrorResponse {
  message?: string;
  error?: string;
  path?: string;
  timestamp?: string;
  correlationId?: string;
}

export interface Workspace {
  id: string;
  name: string;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface JiraConnection {
  id: string;
  workspaceId: string;
  baseUrl: string;
  authType: string;
  status: string;
  clientEmailOrUsername?: string | null;
  externalAccountId?: string | null;
  externalAccountDisplayName?: string | null;
  externalAccountAvatarUrl?: string | null;
  tokenExpiresAt?: string | null;
  lastTestedAt?: string | null;
  createdAt: string;
  updatedAt?: string | null;
}

export interface JiraConnectionSummary {
  id: string;
  workspaceId: string;
  baseUrl: string;
  authType: string;
  status: string;
  externalAccountDisplayName?: string | null;
  externalAccountAvatarUrl?: string | null;
  lastTestedAt?: string | null;
  createdAt: string;
}

export interface JiraOAuthStartResponse {
  connectionId: string;
  state: string;
  authorizationUrl: string;
}

export interface JiraConnectionTestResponse {
  success: boolean;
  message: string;
  accountId?: string | null;
  displayName?: string | null;
  emailAddress?: string | null;
}

export interface Artifact {
  id: string;
  workspaceId: string;
  artifactType: string;
  status: string;
  referenceType: string;
  referenceId: string;
  structuredContent: unknown;
  renderedMarkdown?: string | null;
  title?: string | null;
  summary?: string | null;
  generatorType?: string | null;
  generatorVersion?: string | null;
  generatedAt?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ArtifactSummary {
  id: string;
  workspaceId: string;
  artifactType: string;
  status: string;
  referenceType: string;
  referenceId: string;
  title?: string | null;
  summary?: string | null;
  generatorType?: string | null;
  generatedAt?: string | null;
  createdAt: string;
}

export interface ArtifactListResponse {
  artifacts: ArtifactSummary[];
}

export interface LatestArtifactResponse {
  artifact: Artifact | null;
}

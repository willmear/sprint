import { apiClient } from "@/lib/api/client";
import { env } from "@/config/env";
import type {
  JiraConnection,
  JiraConnectionSummary,
  JiraConnectionTestResponse,
  JiraOAuthStartResponse,
} from "@/types/api";

export const jiraService = {
  listConnections: (workspaceId: string) =>
    apiClient<JiraConnectionSummary[]>(`/api/workspaces/${workspaceId}/jira/connections`),
  getConnection: (workspaceId: string, connectionId: string) =>
    apiClient<JiraConnection>(`/api/workspaces/${workspaceId}/jira/connections/${connectionId}`),
  startOAuth: (workspaceId: string, payload: { baseUrl: string }) =>
    apiClient<JiraOAuthStartResponse>(`/api/workspaces/${workspaceId}/jira/connections/oauth/start`, {
      method: "POST",
      body: {
        ...payload,
        redirectUri: `${env.apiBaseUrl}/api/jira/oauth/callback`,
      },
    }),
  testConnection: (workspaceId: string, connectionId: string) =>
    apiClient<JiraConnectionTestResponse>(`/api/workspaces/${workspaceId}/jira/connections/${connectionId}/test`, {
      method: "POST",
      body: {},
    }),
  disconnect: (workspaceId: string, connectionId: string) =>
    apiClient<JiraConnection>(`/api/workspaces/${workspaceId}/jira/connections/${connectionId}`, {
      method: "DELETE",
    }),
  remove: (workspaceId: string, connectionId: string) =>
    apiClient<void>(`/api/workspaces/${workspaceId}/jira/connections/${connectionId}/remove`, {
      method: "DELETE",
    }),
};

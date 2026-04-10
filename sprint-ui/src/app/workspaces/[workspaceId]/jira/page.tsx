"use client";

import Image from "next/image";
import { use } from "react";

import { JiraConnectionForm } from "@/components/forms/jira-connection-form";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { useDisconnectConnection, useJiraConnections, useRemoveConnection, useTestConnection } from "@/lib/hooks/use-jira";

type WorkspaceJiraSearchParams = {
  jiraOAuth?: string;
  connectionId?: string;
  status?: string;
  accountName?: string;
  message?: string;
};

export default function WorkspaceJiraPage({
  params,
  searchParams,
}: {
  params: Promise<{ workspaceId: string }>;
  searchParams: Promise<WorkspaceJiraSearchParams>;
}) {
  const { workspaceId } = use(params);
  const oauthParams = use(searchParams);
  const connections = useJiraConnections(workspaceId);
  const testConnection = useTestConnection(workspaceId);
  const disconnect = useDisconnectConnection(workspaceId);
  const remove = useRemoveConnection(workspaceId);
  const oauthSucceeded = oauthParams.jiraOAuth === "success";
  const oauthFailed = oauthParams.jiraOAuth === "error";
  const successMessage = oauthParams.accountName
    ? `Connected Jira account ${oauthParams.accountName}.`
    : "Jira OAuth completed successfully.";
  const errorMessage = oauthParams.message ?? "Jira OAuth failed.";

  return (
    <div className="space-y-6">
      <header>
        <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Jira connection</p>
        <h1 className="mt-2 font-display text-4xl font-bold text-ink">Log in to Jira</h1>
        <p className="mt-2 max-w-2xl text-sm text-stone-600">
          Sign in with Jira, verify the account, and sign out when you want to disconnect this workspace.
        </p>
      </header>

      {oauthSucceeded ? (
        <div className="rounded-3xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-800">
          {successMessage}
          {oauthParams.status ? ` Connection status: ${oauthParams.status}.` : ""}
        </div>
      ) : null}

      {oauthFailed ? (
        <div className="rounded-3xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">
          {errorMessage}
        </div>
      ) : null}

      <div className="grid gap-6 xl:grid-cols-[0.8fr_1.2fr]">
        <Card>
          <h2 className="font-display text-2xl font-bold text-ink">Jira login</h2>
          <p className="mt-2 text-sm text-stone-600">Use Atlassian OAuth to sign in to the Jira account that owns the sprint data.</p>
          <div className="mt-6">
            <JiraConnectionForm workspaceId={workspaceId} />
          </div>
        </Card>

        <Card>
          <h2 className="font-display text-2xl font-bold text-ink">Logged in Jira accounts</h2>
          <p className="mt-2 text-sm text-stone-600">Use “Test” before syncing if you suspect a token or account issue.</p>

          <div className="mt-6 space-y-4">
            {connections.isLoading ? <p className="text-sm text-stone-600">Loading connections...</p> : null}
            {connections.error ? <p className="text-sm text-rose-600">{connections.error.message}</p> : null}
            {connections.data?.map((connection) => (
              <div key={connection.id} className="rounded-3xl border border-line bg-cloud p-5">
                <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                  <div className="flex items-center gap-4">
                    <Avatar
                      avatarUrl={connection.externalAccountAvatarUrl}
                      displayName={connection.externalAccountDisplayName || connection.baseUrl}
                    />
                    <div>
                      <div className="flex flex-wrap items-center gap-3">
                        <h3 className="text-lg font-semibold text-ink">{connection.externalAccountDisplayName || connection.baseUrl}</h3>
                        <Badge tone={connection.status === "ACTIVE" ? "success" : "warning"}>
                          {connection.status === "ACTIVE" ? "Logged in" : connection.status}
                        </Badge>
                      </div>
                      <p className="mt-2 text-sm text-stone-600">{connection.baseUrl}</p>
                      {connection.lastTestedAt ? (
                        <p className="mt-1 text-xs uppercase tracking-[0.14em] text-stone-500">
                          Last tested {new Date(connection.lastTestedAt).toLocaleString()}
                        </p>
                      ) : null}
                    </div>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    <Button
                      variant="secondary"
                      disabled={testConnection.isPending}
                      onClick={() => testConnection.mutate(connection.id)}
                    >
                      Test
                    </Button>
                    {connection.status === "REVOKED" ? (
                      <Button
                        variant="ghost"
                        disabled={remove.isPending}
                        onClick={() => remove.mutate(connection.id)}
                      >
                        Remove
                      </Button>
                    ) : (
                      <Button
                        variant="ghost"
                        disabled={disconnect.isPending}
                        onClick={() => disconnect.mutate(connection.id)}
                      >
                        Log out
                      </Button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>

          {testConnection.data ? (
            <div className="mt-4 rounded-3xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-800">
              {testConnection.data.message}
            </div>
          ) : null}
          {testConnection.error ? <p className="mt-4 text-sm text-rose-600">{testConnection.error.message}</p> : null}
          {disconnect.error ? <p className="mt-4 text-sm text-rose-600">{disconnect.error.message}</p> : null}
          {remove.error ? <p className="mt-4 text-sm text-rose-600">{remove.error.message}</p> : null}
        </Card>
      </div>
    </div>
  );
}

function Avatar({ avatarUrl, displayName }: { avatarUrl?: string | null; displayName: string }) {
  if (avatarUrl) {
    return (
      <Image
        alt={displayName}
        className="rounded-full border border-line object-cover"
        height={48}
        loader={({ src }) => src}
        src={avatarUrl}
        unoptimized
        width={48}
      />
    );
  }

  return (
    <div className="flex h-12 w-12 items-center justify-center rounded-full border border-line bg-sand text-sm font-semibold text-ink">
      {displayName.slice(0, 1).toUpperCase()}
    </div>
  );
}

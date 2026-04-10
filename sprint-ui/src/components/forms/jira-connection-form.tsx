"use client";

import { FormEvent, useState } from "react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useStartOAuth } from "@/lib/hooks/use-jira";

export function JiraConnectionForm({ workspaceId }: { workspaceId: string }) {
  const startOAuth = useStartOAuth(workspaceId);
  const [baseUrl, setBaseUrl] = useState("https://your-domain.atlassian.net");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const response = await startOAuth.mutateAsync({ baseUrl });
    window.location.assign(response.authorizationUrl);
  }

  return (
    <form className="space-y-4" onSubmit={handleSubmit}>
      <div className="space-y-2">
        <label className="text-sm font-medium text-stone-700" htmlFor="jira-base-url">
          Jira site URL
        </label>
        <Input
          id="jira-base-url"
          placeholder="https://acme.atlassian.net"
          required
          value={baseUrl}
          onChange={(event) => setBaseUrl(event.target.value)}
        />
      </div>
      <Button disabled={startOAuth.isPending} type="submit">
        {startOAuth.isPending ? "Starting..." : "Connect Jira"}
      </Button>
      {startOAuth.error ? <p className="text-sm text-rose-600">{startOAuth.error.message}</p> : null}
      <p className="text-sm text-stone-600">This sends you to Atlassian OAuth and returns you to this workspace when the flow completes.</p>
    </form>
  );
}

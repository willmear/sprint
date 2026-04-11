"use client";

import Image from "next/image";
import Link from "next/link";
import { usePathname, useSearchParams } from "next/navigation";
import { type ReactNode, useEffect, useMemo, useState } from "react";

import { SidebarNav } from "@/components/layout/sidebar-nav";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useDisconnectConnection, useJiraConnections, useStartOAuth } from "@/lib/hooks/use-jira";

export function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const workspaceId = useMemo(() => resolveWorkspaceId(pathname, searchParams.get("workspaceId")), [pathname, searchParams]);
  const connections = useJiraConnections(workspaceId ?? undefined);
  const startOAuth = useStartOAuth(workspaceId ?? "");
  const disconnect = useDisconnectConnection(workspaceId ?? "");
  const activeConnection = connections.data?.find((connection) => connection.status === "ACTIVE") ?? null;
  const fallbackConnection = connections.data?.[0] ?? null;
  const [baseUrl, setBaseUrl] = useState("https://your-domain.atlassian.net");

  useEffect(() => {
    if (fallbackConnection?.baseUrl) {
      setBaseUrl(fallbackConnection.baseUrl);
    }
  }, [fallbackConnection?.baseUrl]);

  async function handleLogin() {
    if (!workspaceId) {
      return;
    }
    const response = await startOAuth.mutateAsync({ baseUrl });
    window.location.assign(response.authorizationUrl);
  }

  function handleLogout() {
    if (!activeConnection) {
      return;
    }
    disconnect.mutate(activeConnection.id);
  }

  const isFullscreenEditor = /^\/workspaces\/[^/]+\/sprints\/[^/]+\/slides$/.test(pathname);

  if (isFullscreenEditor) {
    return <div className="min-h-screen bg-[#eceff3]">{children}</div>;
  }

  return (
    <div className="mx-auto flex min-h-screen max-w-[1600px] gap-6 px-6 py-8">
      <aside className="hidden w-64 shrink-0 rounded-[32px] border border-white/50 bg-white/70 p-4 shadow-panel backdrop-blur lg:block">
        <div className="mb-8 rounded-[28px] bg-mesh-radial px-4 py-6">
          <p className="font-display text-2xl font-bold text-ink">Sprint Studio</p>
          <p className="mt-2 text-sm text-stone-700">Review delivery, sync Jira, and turn sprint data into a presentation-ready narrative.</p>
        </div>
        <SidebarNav />
      </aside>
      <div className="min-w-0 flex-1">
        <div className="mb-6 flex justify-end">
          <JiraSessionControl
            activeConnection={activeConnection}
            baseUrl={baseUrl}
            canManage={Boolean(workspaceId)}
            disconnectError={disconnect.error?.message ?? null}
            disconnectPending={disconnect.isPending}
            loginError={startOAuth.error?.message ?? null}
            loginPending={startOAuth.isPending}
            onBaseUrlChange={setBaseUrl}
            onLogin={handleLogin}
            onLogout={handleLogout}
            workspaceId={workspaceId}
          />
        </div>
        {children}
      </div>
    </div>
  );
}

function JiraSessionControl({
  activeConnection,
  baseUrl,
  canManage,
  disconnectError,
  disconnectPending,
  loginError,
  loginPending,
  onBaseUrlChange,
  onLogin,
  onLogout,
  workspaceId,
}: {
  activeConnection: {
    id: string;
    baseUrl: string;
    externalAccountAvatarUrl?: string | null;
    externalAccountDisplayName?: string | null;
  } | null;
  baseUrl: string;
  canManage: boolean;
  disconnectError: string | null;
  disconnectPending: boolean;
  loginError: string | null;
  loginPending: boolean;
  onBaseUrlChange: (value: string) => void;
  onLogin: () => Promise<void>;
  onLogout: () => void;
  workspaceId: string | null;
}) {
  if (!canManage || !workspaceId) {
    return (
      <Link href="/workspaces">
        <Button variant="secondary">Choose workspace</Button>
      </Link>
    );
  }

  if (activeConnection) {
    return (
      <div className="rounded-[28px] border border-white/60 bg-white/80 px-4 py-3 shadow-panel backdrop-blur">
        <div className="flex items-center gap-3">
          <Avatar
            avatarUrl={activeConnection.externalAccountAvatarUrl}
            displayName={activeConnection.externalAccountDisplayName || activeConnection.baseUrl}
          />
          <div className="min-w-0">
            <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Jira</p>
            <p className="truncate text-sm font-semibold text-ink">
              {activeConnection.externalAccountDisplayName || activeConnection.baseUrl}
            </p>
          </div>
          <Button disabled={disconnectPending} variant="ghost" onClick={onLogout}>
            {disconnectPending ? "Logging out..." : "Log out"}
          </Button>
        </div>
        {disconnectError ? <p className="mt-2 text-right text-xs text-rose-600">{disconnectError}</p> : null}
      </div>
    );
  }

  return (
    <div className="w-full max-w-xl rounded-[28px] border border-white/60 bg-white/80 px-4 py-3 shadow-panel backdrop-blur">
      <div className="flex flex-col gap-3 md:flex-row md:items-center">
        <div className="min-w-0 md:w-44">
          <p className="text-xs uppercase tracking-[0.16em] text-stone-500">Jira</p>
          <p className="text-sm font-semibold text-ink">Log in to Jira</p>
        </div>
        <Input
          className="flex-1"
          placeholder="https://acme.atlassian.net"
          value={baseUrl}
          onChange={(event) => onBaseUrlChange(event.target.value)}
        />
        <Button disabled={loginPending} onClick={() => void onLogin()}>
          {loginPending ? "Starting..." : "Log in"}
        </Button>
      </div>
      {loginError ? <p className="mt-2 text-right text-xs text-rose-600">{loginError}</p> : null}
    </div>
  );
}

function Avatar({ avatarUrl, displayName }: { avatarUrl?: string | null; displayName: string }) {
  if (avatarUrl) {
    return (
      <Image
        alt={displayName}
        className="rounded-full border border-line object-cover"
        height={40}
        loader={({ src }) => src}
        src={avatarUrl}
        unoptimized
        width={40}
      />
    );
  }

  return (
    <div className="flex h-10 w-10 items-center justify-center rounded-full border border-line bg-sand text-sm font-semibold text-ink">
      {displayName.slice(0, 1).toUpperCase()}
    </div>
  );
}

function resolveWorkspaceId(pathname: string, workspaceIdFromQuery: string | null): string | null {
  const pathSegments = pathname.split("/").filter(Boolean);

  if (pathSegments[0] === "workspaces" && pathSegments[1] && pathSegments[1] !== "new") {
    return pathSegments[1];
  }

  if ((pathSegments[0] === "review" || pathSegments[0] === "sprints") && pathSegments[1]) {
    return pathSegments[1];
  }

  if (pathname === "/sprints" && workspaceIdFromQuery) {
    return workspaceIdFromQuery;
  }

  return null;
}

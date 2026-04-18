"use client";

import { usePathname } from "next/navigation";
import { type ReactNode } from "react";

import { SidebarNav } from "@/components/layout/sidebar-nav";
import { UserMenu } from "@/components/layout/user-menu";
import { useAuth } from "@/lib/hooks/use-auth";

export function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const auth = useAuth();

  const isFullscreenEditor = /^\/workspaces\/[^/]+\/sprints\/[^/]+\/slides$/.test(pathname);
  const isLoginRoute = pathname === "/login";

  if (isFullscreenEditor) {
    return <div className="h-screen overflow-hidden bg-[#eceff3]">{children}</div>;
  }

  if (isLoginRoute) {
    return (
      <div className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(55,94,76,0.14),_transparent_38%),linear-gradient(180deg,_#f5f1e8_0%,_#ece7de_100%)]">
        <div className="mx-auto flex min-h-screen max-w-6xl items-center justify-center px-6 py-10">{children}</div>
      </div>
    );
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
        <div className="mb-6 flex items-start justify-between gap-4">
          <div>
            <p className="text-xs uppercase tracking-[0.24em] text-stone-500">Authenticated workspace flow</p>
            <p className="mt-2 text-sm text-stone-600">Log in first, then work with your own workspaces and Jira-connected delivery data.</p>
          </div>
          {auth.user ? <UserMenu user={auth.user} /> : null}
        </div>
        {children}
      </div>
    </div>
  );
}

"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";

import { Button } from "@/components/ui/button";
import { useLogout } from "@/lib/hooks/use-auth";
import type { AuthUser } from "@/types/auth";

export function UserMenu({ user }: { user: AuthUser }) {
  const router = useRouter();
  const logout = useLogout();

  async function handleLogout() {
    await logout.mutateAsync();
    router.replace("/login");
  }

  return (
    <div className="rounded-[28px] border border-white/60 bg-white/80 px-4 py-3 shadow-panel backdrop-blur">
      <div className="flex items-center gap-3">
        <Avatar avatarUrl={user.avatarUrl} displayName={user.displayName} />
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-ink">{user.displayName}</p>
          <p className="truncate text-xs text-stone-500">{user.email || "Atlassian account"}</p>
        </div>
        <Button disabled={logout.isPending} variant="ghost" onClick={() => void handleLogout()}>
          {logout.isPending ? "Logging out..." : "Log out"}
        </Button>
      </div>
      {logout.error ? <p className="mt-2 text-right text-xs text-rose-600">{logout.error.message}</p> : null}
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

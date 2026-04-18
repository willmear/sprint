"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";

import { useAuth } from "@/lib/hooks/use-auth";

export default function HomePage() {
  const router = useRouter();
  const auth = useAuth();

  useEffect(() => {
    if (auth.isLoading) {
      return;
    }
    router.replace(auth.authenticated ? "/workspaces" : "/login");
  }, [auth.authenticated, auth.isLoading, router]);

  return (
    <div className="rounded-[28px] border border-white/60 bg-white/80 px-6 py-5 text-sm text-stone-600 shadow-panel">
      Checking session...
    </div>
  );
}

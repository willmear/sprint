"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";

import { LoginCard } from "@/components/auth/login-card";
import { useAuth } from "@/lib/hooks/use-auth";

export default function LoginPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const auth = useAuth();
  const redirectTo = searchParams.get("redirectTo") || "/workspaces";

  useEffect(() => {
    if (!auth.isLoading && auth.authenticated) {
      router.replace(redirectTo);
    }
  }, [auth.authenticated, auth.isLoading, redirectTo, router]);

  if (auth.isLoading || auth.authenticated) {
    return (
      <div className="rounded-[28px] border border-white/60 bg-white/85 px-6 py-5 text-sm text-stone-600 shadow-panel">
        Checking session...
      </div>
    );
  }

  return <LoginCard redirectTo={redirectTo} />;
}

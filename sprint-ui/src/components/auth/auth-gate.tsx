"use client";

import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { type ReactNode, useEffect, useMemo } from "react";

import { useAuth } from "@/lib/hooks/use-auth";

const protectedPatterns = [/^\/workspaces(\/|$)/, /^\/sprints(\/|$)/, /^\/review(\/|$)/, /^\/jobs(\/|$)/, /^\/profile(\/|$)/];

export function AuthGate({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const router = useRouter();
  const auth = useAuth();
  const isProtectedRoute = useMemo(() => protectedPatterns.some((pattern) => pattern.test(pathname)), [pathname]);

  useEffect(() => {
    if (auth.isLoading) {
      return;
    }
    if (isProtectedRoute && !auth.authenticated) {
      const suffix = searchParams.toString();
      const redirectTo = suffix ? `${pathname}?${suffix}` : pathname;
      router.replace(`/login?redirectTo=${encodeURIComponent(redirectTo)}`);
    }
  }, [auth.authenticated, auth.isLoading, isProtectedRoute, pathname, router, searchParams]);

  if (auth.isLoading && isProtectedRoute) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-[#f4f1ec] px-6">
        <div className="rounded-[28px] border border-line bg-white px-6 py-5 text-sm text-stone-600 shadow-panel">
          Checking session...
        </div>
      </div>
    );
  }

  if (isProtectedRoute && !auth.authenticated) {
    return null;
  }

  return <>{children}</>;
}

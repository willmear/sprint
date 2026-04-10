"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { cn } from "@/lib/utils/cn";

const items = [
  { href: "/", label: "Overview" },
  { href: "/workspaces", label: "Workspaces" },
  { href: "/sprints", label: "Sprints" },
  { href: "/jobs", label: "Jobs" },
];

export function SidebarNav() {
  const pathname = usePathname();

  return (
    <nav className="flex flex-col gap-2">
      {items.map((item) => {
        const active = pathname === item.href || pathname.startsWith(`${item.href}/`);
        return (
          <Link
            key={item.href}
            href={item.href}
            className={cn(
              "rounded-2xl px-4 py-3 text-sm font-medium transition",
              active ? "bg-ink text-sand" : "text-stone-700 hover:bg-white/80"
            )}
          >
            {item.label}
          </Link>
        );
      })}
    </nav>
  );
}

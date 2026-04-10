import * as React from "react";

import { cn } from "@/lib/utils/cn";

export function Card({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("rounded-[28px] border border-line bg-white/90 p-6 shadow-panel backdrop-blur", className)}
      {...props}
    />
  );
}

import * as React from "react";
import { cn } from "@/lib/utils/cn";

export const Separator = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div ref={ref} className={cn("my-6 h-px bg-slate-200", className)} {...props} />
  )
);
Separator.displayName = "Separator";

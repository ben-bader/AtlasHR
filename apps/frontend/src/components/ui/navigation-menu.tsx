import * as React from "react";
import { cn } from "@/lib/utils/cn";

export type NavigationMenuItemProps = {
  href: string;
  title: string;
  description: string;
  icon?: React.ReactNode;
  active?: boolean;
};

export function NavigationMenuItem({ href, title, description, icon, active }: NavigationMenuItemProps) {
  return (
    <a
      href={href}
      className={cn(
        "group flex items-start gap-3 rounded-3xl border border-transparent p-4 text-slate-900 transition hover:border-slate-200 hover:bg-slate-50",
        active && "border-brand-500 bg-brand-50"
      )}
    >
      <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-slate-100 text-brand-600">{icon}</div>
      <div className="space-y-1">
        <p className="text-sm font-semibold">{title}</p>
        <p className="text-sm leading-6 text-slate-600">{description}</p>
      </div>
    </a>
  );
}

export function NavigationMenu({ children }: { children: React.ReactNode }) {
  return <nav className="space-y-2">{children}</nav>;
}

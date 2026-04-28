"use client";

import { ReactNode } from "react";
import { useProtectedRoute } from "@/lib/hooks/useProtectedRoute";
import { DashboardShell } from "@/components/layout/dashboard-shell";

export default function DashboardLayout({ children }: { children: ReactNode }) {
  const { isLoading, isReady } = useProtectedRoute();

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50">
        <div className="inline-flex h-16 w-16 items-center justify-center rounded-full bg-brand-100 text-brand-700">Loading...</div>
      </div>
    );
  }

  if (!isReady) {
    return null;
  }

  return <DashboardShell>{children}</DashboardShell>;
}

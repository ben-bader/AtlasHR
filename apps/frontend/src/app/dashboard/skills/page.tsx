"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { ErrorBoundary } from "@/components/error-boundary"
import { DashboardPageShell } from "@/components/dashboard/dashboard-page-shell"
import { SkillsAdminView } from "@/components/hr/skills-admin-view"

export default function SkillsPage() {
  useProtectedRoute()

  return (
    <div>
      <DashboardPageShell
        crumbs={[
          { label: "Dashboard", href: "/dashboard" },
          { label: "Skills" },
        ]}
      >
        <ErrorBoundary>
          <SkillsAdminView />
        </ErrorBoundary>
      </DashboardPageShell>
    </div>
  )
}

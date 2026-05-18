"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { ErrorBoundary } from "@/app/components/error-boundary"
import { DashboardPageShell } from "@/app/components/dashboard/dashboard-page-shell"
import { SkillsAdminView } from "@/app/components/hr/skills-admin-view"

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

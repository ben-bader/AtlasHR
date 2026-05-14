"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
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
        <SkillsAdminView />
      </DashboardPageShell>
    </div>
  )
}

"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { DashboardPageShell } from "@/components/dashboard/dashboard-page-shell"
import { DesignationsAdminView } from "@/components/hr/designations-admin-view"

export default function DesignationsPage() {
  useProtectedRoute()

  return (
    <div>
      <DashboardPageShell
        crumbs={[
          { label: "Dashboard", href: "/dashboard" },
          { label: "Organization", href: "/dashboard/departments" },
          { label: "Designations" },
        ]}
      >
        <DesignationsAdminView />
      </DashboardPageShell>
    </div>
  )
}

"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { ErrorBoundary } from "@/app/components/error-boundary"
import { DashboardPageShell } from "@/app/components/dashboard/dashboard-page-shell"
import { DesignationsAdminView } from "@/app/components/hr/designations-admin-view"

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
        <ErrorBoundary>
          <DesignationsAdminView />
        </ErrorBoundary>
      </DashboardPageShell>
    </div>
  )
}

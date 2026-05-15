"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { ErrorBoundary } from "@/app/components/error-boundary"
import { DashboardPageShell } from "@/app/components/dashboard/dashboard-page-shell"
import { DepartmentsAdminView } from "@/app/components/hr/departments-admin-view"

export default function DepartmentsPage() {
  useProtectedRoute()

  return (
    <div>
      <DashboardPageShell
        crumbs={[
          { label: "Dashboard", href: "/dashboard" },
          { label: "Organization", href: "/dashboard/departments" },
          { label: "Departments" },
        ]}
      >
        <ErrorBoundary>
          <DepartmentsAdminView />
        </ErrorBoundary>
      </DashboardPageShell>
    </div>
  )
}

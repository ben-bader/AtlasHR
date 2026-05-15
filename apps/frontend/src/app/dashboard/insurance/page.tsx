"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { ErrorBoundary } from "@/components/error-boundary"
import { DashboardPageShell } from "@/components/dashboard/dashboard-page-shell"
import { InsuranceAdminView } from "@/components/hr/insurance-admin-view"

export default function InsurancePage() {
  useProtectedRoute()

  return (
    <div>
      <DashboardPageShell
        crumbs={[
          { label: "Dashboard", href: "/dashboard" },
          { label: "Insurance" },
        ]}
      >
        <ErrorBoundary>
          <InsuranceAdminView />
        </ErrorBoundary>
      </DashboardPageShell>
    </div>
  )
}

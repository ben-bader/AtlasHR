"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { ErrorBoundary } from "@/app/components/error-boundary"
import { DashboardPageShell } from "@/app/components/dashboard/dashboard-page-shell"
import { InsuranceAdminView } from "@/app/components/hr/insurance-admin-view"

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

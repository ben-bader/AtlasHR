"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { ErrorBoundary } from "@/components/error-boundary"
import { DashboardPageShell } from "@/components/dashboard/dashboard-page-shell"
import { OrganizationChartView } from "@/components/hr/organization-chart-view"

export default function OrganizationPage() {
  useProtectedRoute()

  return (
    <div>
      <DashboardPageShell
        crumbs={[
          { label: "Dashboard", href: "/dashboard" },
          { label: "Organization", href: "/dashboard/departments" },
          { label: "Org chart" },
        ]}
      >
        <ErrorBoundary>
          <OrganizationChartView />
        </ErrorBoundary>
      </DashboardPageShell>
    </div>
  )
}

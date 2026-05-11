"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
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
        <InsuranceAdminView />
      </DashboardPageShell>
    </div>
  )
}

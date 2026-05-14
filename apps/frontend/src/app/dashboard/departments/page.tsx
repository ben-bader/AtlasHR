"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { DashboardPageShell } from "@/components/dashboard/dashboard-page-shell"
import { DepartmentsAdminView } from "@/components/hr/departments-admin-view"

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
        <DepartmentsAdminView />
      </DashboardPageShell>
    </div>
  )
}

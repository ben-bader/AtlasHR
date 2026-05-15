"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { DashboardPageShell } from "@/components/dashboard/dashboard-page-shell"
import { ProfileView } from "@/components/profile/profile-view"

export default function ProfilePage() {
  useProtectedRoute()

  return (
    <div>
      <DashboardPageShell
        crumbs={[
          { label: "Dashboard", href: "/dashboard" },
          { label: "Profile" },
        ]}
      >
        <ProfileView />
      </DashboardPageShell>
    </div>
  )
}

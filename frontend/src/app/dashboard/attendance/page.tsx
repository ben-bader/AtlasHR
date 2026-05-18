"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { DashboardPageShell } from "@/app/components/dashboard/dashboard-page-shell"
import { DailyAttendanceView } from "@/app/components/attendance/daily-attendance-view"

export default function AttendancePage() {
  useProtectedRoute()

  return (
    <div>
      <DashboardPageShell
        crumbs={[
          { label: "Dashboard", href: "/dashboard" },
          { label: "Attendance" },
        ]}
      >
        <DailyAttendanceView />
      </DashboardPageShell>
    </div>
  )
}

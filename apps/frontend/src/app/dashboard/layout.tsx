"use client"

import { AppSidebar } from "@/app/components/app-sidebar"
import { SidebarInset, SidebarProvider } from "@/app/components/ui/sidebar"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>{children}</SidebarInset>
    </SidebarProvider>
  )
}

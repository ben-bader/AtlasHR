"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { useAuth } from "@/hooks/useAuth"
import { useEmployeeList } from "@/hooks/useEmployee"
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarTrigger } from "@/components/ui/sidebar"
import { Button } from "@/components/ui/button"
import { useRouter } from "next/navigation"
import { Users, BarChart3, Settings } from "lucide-react"

export default function DashboardPage() {
  useProtectedRoute()
  const router = useRouter()
  const { user, logout } = useAuth()
  const { employees, total } = useEmployeeList({ pageSize: 1 })

  return (
    <>
        <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
          <div className="flex items-center gap-2 px-4 w-full justify-between">
            <div className="flex items-center gap-2">
              <SidebarTrigger className="-ml-1" />
              <Separator
                orientation="vertical"
                className="mr-2 data-vertical:h-4 data-vertical:self-auto"
              />
              <Breadcrumb>
                <BreadcrumbList>
                  <BreadcrumbItem className="hidden md:block">
                    <BreadcrumbLink href="/dashboard">
                      Dashboard
                    </BreadcrumbLink>
                  </BreadcrumbItem>
                  <BreadcrumbSeparator className="hidden md:block" />
                  <BreadcrumbItem>
                    <BreadcrumbPage>Overview</BreadcrumbPage>
                  </BreadcrumbItem>
                </BreadcrumbList>
              </Breadcrumb>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-sm text-muted-foreground">
                Welcome, {user?.username || "User"}!
              </span>
              <button
                onClick={logout}
                className="text-sm text-muted-foreground hover:text-foreground transition-colors"
              >
                Logout
              </button>
            </div>
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
          <div className="grid auto-rows-min gap-4 md:grid-cols-3">
            <div className="rounded-lg border border-border bg-background p-6 hover:shadow-md transition-shadow cursor-pointer" onClick={() => router.push("/dashboard/employees")}>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">Total Employees</p>
                  <p className="text-3xl font-bold mt-2">{total || 0}</p>
                </div>
                <div className="h-12 w-12 rounded-lg bg-blue-100 flex items-center justify-center">
                  <Users className="h-6 w-6 text-blue-600" />
                </div>
              </div>
            </div>
            
            <div className="rounded-lg border border-border bg-background p-6 hover:shadow-md transition-shadow cursor-pointer" onClick={() => router.push("/dashboard/employees?status=ACTIVE")}>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">Active Employees</p>
                  <p className="text-3xl font-bold mt-2">-</p>
                </div>
                <div className="h-12 w-12 rounded-lg bg-green-100 flex items-center justify-center">
                  <BarChart3 className="h-6 w-6 text-green-600" />
                </div>
              </div>
            </div>
            
            <div className="rounded-lg border border-border bg-background p-6 hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">Quick Actions</p>
                  <Button 
                    variant="outline" 
                    size="sm" 
                    className="mt-2"
                    onClick={() => router.push("/dashboard/employees/create")}
                  >
                    Add Employee
                  </Button>
                </div>
                <div className="h-12 w-12 rounded-lg bg-purple-100 flex items-center justify-center">
                  <Settings className="h-6 w-6 text-purple-600" />
                </div>
              </div>
            </div>
          </div>
          
          <div className="min-h-[100vh] flex-1 rounded-xl bg-muted/50 md:min-h-min p-6">
            <h2 className="text-2xl font-bold mb-4">Welcome to AtlasHR</h2>
            <p className="text-muted-foreground mb-6">
              Manage your HR operations efficiently with our comprehensive employee management system. 
              Track employees, manage information, and streamline your HR workflows all in one place.
            </p>
            <div className="space-y-4">
              <div className="p-4 border border-border rounded-lg">
                <h3 className="font-semibold mb-2">Getting Started</h3>
                <ul className="list-disc list-inside space-y-2 text-sm text-muted-foreground">
                  <li>Add new employees to the system</li>
                  <li>View and update employee information</li>
                  <li>Search and filter employees by various criteria</li>
                  <li>Manage employee lifecycle from onboarding to offboarding</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
    </>
  )
}

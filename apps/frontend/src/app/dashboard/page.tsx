"use client"

import { useProtectedRoute } from "@/hooks/useProtectedRoute"
import { useAuth } from "@/hooks/useAuth"
import { useEmployeeList } from "@/hooks/useEmployee"
import { ErrorBoundary } from "@/components/error-boundary"
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
import { Users, BarChart3, Settings, Building2, BadgeCheck, Network, Award, Shield } from "lucide-react"

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
                onClick={() => { logout() ; router.push("/")}}
                className="text-sm text-muted-foreground hover:text-foreground transition-colors"
              >
                Logout
              </button>
            </div>
          </div>
        </header>
        <ErrorBoundary>
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
                  <p className="text-3xl font-bold mt-2">{activeTotal || 0}</p>
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
          
          <div className="min-h-[100vh] flex-1 rounded-xl bg-muted/50 md:min-h-min p-6 space-y-6">
            <div>
              <h2 className="text-2xl font-bold mb-2">Welcome to AtlasHR</h2>
              <p className="text-muted-foreground text-sm">
                Employee service modules available through the API gateway (authenticated).
              </p>
            </div>
            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
              <button
                type="button"
                onClick={() => router.push("/dashboard/departments")}
                className="flex items-center gap-3 rounded-lg border border-border bg-background p-4 text-left hover:bg-muted/60 transition-colors"
              >
                <Building2 className="h-8 w-8 text-blue-600 shrink-0" />
                <div>
                  <p className="font-medium">Departments</p>
                  <p className="text-xs text-muted-foreground">CRUD · sub-departments</p>
                </div>
              </button>
              <button
                type="button"
                onClick={() => router.push("/dashboard/designations")}
                className="flex items-center gap-3 rounded-lg border border-border bg-background p-4 text-left hover:bg-muted/60 transition-colors"
              >
                <BadgeCheck className="h-8 w-8 text-violet-600 shrink-0" />
                <div>
                  <p className="font-medium">Designations</p>
                  <p className="text-xs text-muted-foreground">Job titles & levels</p>
                </div>
              </button>
              <button
                type="button"
                onClick={() => router.push("/dashboard/organization")}
                className="flex items-center gap-3 rounded-lg border border-border bg-background p-4 text-left hover:bg-muted/60 transition-colors"
              >
                <Network className="h-8 w-8 text-emerald-600 shrink-0" />
                <div>
                  <p className="font-medium">Org chart</p>
                  <p className="text-xs text-muted-foreground">Manager · dept · employee</p>
                </div>
              </button>
              <button
                type="button"
                onClick={() => router.push("/dashboard/skills")}
                className="flex items-center gap-3 rounded-lg border border-border bg-background p-4 text-left hover:bg-muted/60 transition-colors"
              >
                <Award className="h-8 w-8 text-amber-600 shrink-0" />
                <div>
                  <p className="font-medium">Skills</p>
                  <p className="text-xs text-muted-foreground">Search · add skills</p>
                </div>
              </button>
              <button
                type="button"
                onClick={() => router.push("/dashboard/insurance")}
                className="flex items-center gap-3 rounded-lg border border-border bg-background p-4 text-left hover:bg-muted/60 transition-colors"
              >
                <Shield className="h-8 w-8 text-sky-600 shrink-0" />
                <div>
                  <p className="font-medium">Insurance</p>
                  <p className="text-xs text-muted-foreground">Policies per employee</p>
                </div>
              </button>
            </div>
          </div>
        </div>
        </ErrorBoundary>
    </>
  )
}

"use client"

import { useState } from "react"
import { useQuery } from "@tanstack/react-query"
import { Button } from "@/app/components/ui/button"
import { Input } from "@/app/components/ui/input"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/app/components/ui/table"
import { organizationChartAPI } from "@/lib/api/organizationChart"
import type { OrgChartNode } from "@/lib/types"
import { Loader2 } from "lucide-react"
import { FormLabel } from "@/app/components/ui/form"

function empLabel(e?: { firstName?: string; lastName?: string; id?: string } | null) {
  if (!e) return "—"
  const name = [e.firstName, e.lastName].filter(Boolean).join(" ")
  return name ? `${name} (${e.id ?? "?"})` : e.id ?? "—"
}

export function OrganizationChartView() {
  const [mode, setMode] = useState<"department" | "manager" | "employee">("department")
  const [deptId, setDeptId] = useState("")
  const [managerId, setManagerId] = useState("")
  const [employeeId, setEmployeeId] = useState("")
  const [runKey, setRunKey] = useState(0)

  const deptNumeric = Number(deptId)

  const deptQuery = useQuery({
    queryKey: ["org-chart-dept", deptNumeric, runKey],
    queryFn: () => organizationChartAPI.byDepartment(deptNumeric),
    enabled: mode === "department" && runKey > 0 && Number.isFinite(deptNumeric) && deptNumeric > 0,
  })

  const mgrQuery = useQuery({
    queryKey: ["org-chart-mgr", managerId, runKey],
    queryFn: () => organizationChartAPI.byManager(managerId.trim()),
    enabled: mode === "manager" && runKey > 0 && managerId.trim().length > 0,
  })

  const empQuery = useQuery({
    queryKey: ["org-chart-emp", employeeId, runKey],
    queryFn: () => organizationChartAPI.byEmployee(employeeId.trim()),
    enabled: mode === "employee" && runKey > 0 && employeeId.trim().length > 0,
  })

  const run = () => setRunKey((k) => k + 1)

  const list: OrgChartNode[] =
    mode === "department"
      ? deptQuery.data ?? []
      : mode === "manager"
        ? mgrQuery.data ?? []
        : empQuery.data
          ? [empQuery.data]
          : []

  const loading =
    mode === "department"
      ? deptQuery.isFetching
      : mode === "manager"
        ? mgrQuery.isFetching
        : empQuery.isFetching

  return (
    <div className="space-y-6">
      <p className="text-sm text-muted-foreground">
        OrganizationChartController — browse hierarchy by department, manager, or employee.
      </p>

      <div className="flex flex-wrap gap-2">
        {(["department", "manager", "employee"] as const).map((m) => (
          <Button key={m} variant={mode === m ? "default" : "outline"} size="sm" onClick={() => setMode(m)}>
            By {m}
          </Button>
        ))}
      </div>

      <div className="flex flex-wrap items-end gap-4 rounded-lg border border-border bg-background p-4">
        {mode === "department" && (
          <div className="grid gap-1">
            <FormLabel>Department ID</FormLabel>
            <Input value={deptId} onChange={(e) => setDeptId(e.target.value)} placeholder="e.g. 1" className="w-48" />
          </div>
        )}
        {mode === "manager" && (
          <div className="grid gap-1">
            <FormLabel>Manager employee ID</FormLabel>
            <Input value={managerId} onChange={(e) => setManagerId(e.target.value)} placeholder="EMP..." className="w-56" />
          </div>
        )}
        {mode === "employee" && (
          <div className="grid gap-1">
            <FormLabel>Employee ID</FormLabel>
            <Input value={employeeId} onChange={(e) => setEmployeeId(e.target.value)} placeholder="EMP..." className="w-56" />
          </div>
        )}
        <Button onClick={run}>Load</Button>
      </div>

      <div className="rounded-lg border border-border bg-background">
        {loading ? (
          <div className="flex justify-center py-16">
            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Chart row</TableHead>
                <TableHead>Employee</TableHead>
                <TableHead>Manager</TableHead>
                <TableHead>Level</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {runKey === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-10 text-muted-foreground">
                    Enter an id and click Load
                  </TableCell>
                </TableRow>
              ) : list.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-10 text-muted-foreground">
                    No rows returned
                  </TableCell>
                </TableRow>
              ) : (
                list.map((row) => (
                  <TableRow key={row.id}>
                    <TableCell className="font-mono text-xs">{row.id}</TableCell>
                    <TableCell>{empLabel(row.employee)}</TableCell>
                    <TableCell>{empLabel(row.manager)}</TableCell>
                    <TableCell>{row.hierarchyLevel}</TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        )}
      </div>
    </div>
  )
}

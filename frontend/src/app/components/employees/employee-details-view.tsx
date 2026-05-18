"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Button } from "@/app/components/ui/button"
import { Separator } from "@/app/components/ui/separator"
import { Input } from "@/app/components/ui/input"
import { useEmployee } from "@/hooks/useEmployee"
import { skillsAPI } from "@/lib/api/skills"
import { insuranceAPI } from "@/lib/api/insurance"
import { employeeAPI } from "@/lib/api/employee"
import { departmentAPI } from "@/lib/api/department"
import { designationAPI } from "@/lib/api/designation"
import { Loader, ArrowLeft, Edit } from "lucide-react"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/app/components/ui/table"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/app/components/ui/dialog"
import { FormLabel } from "@/app/components/ui/form"
import { cn } from "@/lib/utils"

interface EmployeeDetailsViewProps {
  employeeId: string
}

type TabKey = "profile" | "skills" | "insurance"

const selectClassName = cn(
  "h-9 w-full min-w-0 rounded-md border border-input bg-transparent px-2.5 py-1 text-base shadow-xs outline-none md:text-sm dark:bg-input/30"
)

export function EmployeeDetailsView({ employeeId }: EmployeeDetailsViewProps) {
  const router = useRouter()
  const qc = useQueryClient()
  const [tab, setTab] = useState<TabKey>("profile")
  const [promoteOpen, setPromoteOpen] = useState(false)
  const [transferOpen, setTransferOpen] = useState(false)
  const [promoteForm, setPromoteForm] = useState({
    newDesignationId: "",
    newGrade: "",
    effectiveDate: "",
    reason: "",
  })
  const [transferForm, setTransferForm] = useState({
    newDepartmentId: "",
    newDesignationId: "",
    effectiveDate: "",
    reason: "",
  })

  const { employee, isLoading, error } = useEmployee(employeeId)

  const { data: departments = [] } = useQuery({
    queryKey: ["departments"],
    queryFn: () => departmentAPI.list(),
  })

  const { data: designations = [] } = useQuery({
    queryKey: ["designations"],
    queryFn: () => designationAPI.list(),
  })

  const promoteMut = useMutation({
    mutationFn: () =>
      employeeAPI.promoteEmployee({
        employeeId,
        newDesignationId: promoteForm.newDesignationId,
        newGrade: promoteForm.newGrade || undefined,
        effectiveDate: promoteForm.effectiveDate || undefined,
        reason: promoteForm.reason || undefined,
      }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["employee", employeeId] })
      qc.invalidateQueries({ queryKey: ["employees"] })
      setPromoteOpen(false)
    },
  })

  const transferMut = useMutation({
    mutationFn: () =>
      employeeAPI.transferEmployee({
        employeeId,
        newDepartmentId: Number(transferForm.newDepartmentId),
        newDesignationId: transferForm.newDesignationId || undefined,
        effectiveDate: transferForm.effectiveDate || undefined,
        reason: transferForm.reason || undefined,
      }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["employee", employeeId] })
      qc.invalidateQueries({ queryKey: ["employees"] })
      setTransferOpen(false)
    },
  })

  const { data: skills = [], isLoading: skillsLoading } = useQuery({
    queryKey: ["employee-skills", employeeId],
    queryFn: () => skillsAPI.byEmployee(employeeId),
    enabled: tab === "skills",
  })

  const { data: insurances = [], isLoading: insLoading } = useQuery({
    queryKey: ["employee-insurances", employeeId],
    queryFn: () => insuranceAPI.byEmployee(employeeId),
    enabled: tab === "insurance",
  })

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  if (error || !employee) {
    return (
      <div className="space-y-4">
        <Button variant="outline" onClick={() => router.back()} className="flex items-center gap-2">
          <ArrowLeft className="h-4 w-4" />
          Back
        </Button>
        <div className="p-4 bg-red-50 border border-red-200 rounded text-sm text-red-800">
          Failed to load employee details. Please try again.
        </div>
      </div>
    )
  }

  const tabs: { key: TabKey; label: string }[] = [
    { key: "profile", label: "Profile" },
    { key: "skills", label: "Skills" },
    { key: "insurance", label: "Insurance" },
  ]

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold">
            {employee.firstName} {employee.lastName}
          </h1>
          <p className="text-muted-foreground mt-1">{employee.designation || "Employee"} · {employee.id}</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button variant="outline" onClick={() => router.back()} className="flex items-center gap-2">
            <ArrowLeft className="h-4 w-4" />
            Back
          </Button>
          <Button variant="secondary" onClick={() => setPromoteOpen(true)}>
            Promote
          </Button>
          <Button variant="secondary" onClick={() => setTransferOpen(true)}>
            Transfer
          </Button>
          <Button onClick={() => router.push(`/dashboard/employees/${employeeId}/edit`)} className="flex items-center gap-2">
            <Edit className="h-4 w-4" />
            Edit
          </Button>
        </div>
      </div>

      <div className="flex flex-wrap gap-2 border-b border-border pb-2">
        {tabs.map((t) => (
          <button
            key={t.key}
            type="button"
            onClick={() => setTab(t.key)}
            className={cn(
              "rounded-md px-3 py-1.5 text-sm font-medium transition-colors",
              tab === t.key
                ? "bg-primary text-primary-foreground"
                : "text-muted-foreground hover:bg-muted hover:text-foreground"
            )}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === "profile" && (
        <>
          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-4">
              <div>
                <p className="text-sm text-muted-foreground">Email</p>
                <p className="text-lg font-medium">{employee.email}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Phone</p>
                <p className="text-lg font-medium">{employee.phone || "-"}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Department</p>
                <p className="text-lg font-medium">{employee.department || "-"}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Designation</p>
                <p className="text-lg font-medium">{employee.designation || "-"}</p>
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <p className="text-sm text-muted-foreground">Status</p>
                <p
                  className={`text-lg font-medium inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                    employee.status === "ACTIVE"
                      ? "bg-green-100 text-green-800"
                      : "bg-gray-100 text-gray-800"
                  }`}
                >
                  {employee.status || "ACTIVE"}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Join date</p>
                <p className="text-lg font-medium">
                  {employee.joinDate ? new Date(employee.joinDate).toLocaleDateString() : "-"}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Date of birth</p>
                <p className="text-lg font-medium">
                  {employee.dateOfBirth ? new Date(employee.dateOfBirth).toLocaleDateString() : "-"}
                </p>
              </div>
            </div>
          </div>

          {(employee.address || employee.city || employee.state || employee.zipCode || employee.country) && (
            <>
              <Separator />
              <div>
                <h2 className="text-xl font-semibold mb-4">Address</h2>
                <div className="space-y-2 text-sm">
                  {employee.address && <p>{employee.address}</p>}
                  {(employee.city || employee.state || employee.zipCode) && (
                    <p>
                      {[employee.city, employee.state, employee.zipCode].filter(Boolean).join(", ")}
                    </p>
                  )}
                  {employee.country && <p>{employee.country}</p>}
                </div>
              </div>
            </>
          )}
        </>
      )}

      {tab === "skills" && (
        <div className="rounded-lg border border-border bg-background">
          {skillsLoading ? (
            <div className="flex justify-center py-12">
              <Loader className="h-6 w-6 animate-spin text-muted-foreground" />
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Skill</TableHead>
                  <TableHead>Level</TableHead>
                  <TableHead>Certification</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {skills.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={4} className="text-center text-muted-foreground py-8">
                      No skills recorded
                    </TableCell>
                  </TableRow>
                ) : (
                  skills.map((s) => (
                    <TableRow key={s.skillId}>
                      <TableCell className="font-medium">{s.skillName}</TableCell>
                      <TableCell>{s.competencyLevel || "-"}</TableCell>
                      <TableCell>{s.certification || "-"}</TableCell>
                      <TableCell>{s.status || "-"}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          )}
        </div>
      )}

      {tab === "insurance" && (
        <div className="rounded-lg border border-border bg-background">
          {insLoading ? (
            <div className="flex justify-center py-12">
              <Loader className="h-6 w-6 animate-spin text-muted-foreground" />
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Policy #</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Provider</TableHead>
                  <TableHead>Coverage</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {insurances.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center text-muted-foreground py-8">
                      No insurance policies
                    </TableCell>
                  </TableRow>
                ) : (
                  insurances.map((i) => (
                    <TableRow key={i.insuranceId}>
                      <TableCell className="font-mono text-xs">{i.policyNumber}</TableCell>
                      <TableCell>{i.insuranceType}</TableCell>
                      <TableCell>{i.providerName}</TableCell>
                      <TableCell>{i.coverageAmount ?? "-"}</TableCell>
                      <TableCell>{i.status || "-"}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          )}
        </div>
      )}

      <Dialog open={promoteOpen} onOpenChange={setPromoteOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Promote employee</DialogTitle>
            <DialogDescription>POST /employees/promote</DialogDescription>
          </DialogHeader>
          <div className="grid gap-2 py-2">
            <FormLabel>New designation</FormLabel>
            <select
              className={selectClassName}
              value={promoteForm.newDesignationId}
              onChange={(e) => setPromoteForm((f) => ({ ...f, newDesignationId: e.target.value }))}
            >
              <option value="">Select</option>
              {designations.map((d) => (
                <option key={d.designationId} value={d.designationId}>
                  {d.designationName}
                </option>
              ))}
            </select>
            <FormLabel>Grade</FormLabel>
            <Input value={promoteForm.newGrade} onChange={(e) => setPromoteForm((f) => ({ ...f, newGrade: e.target.value }))} />
            <FormLabel>Effective date</FormLabel>
            <Input type="date" value={promoteForm.effectiveDate} onChange={(e) => setPromoteForm((f) => ({ ...f, effectiveDate: e.target.value }))} />
            <FormLabel>Reason</FormLabel>
            <Input value={promoteForm.reason} onChange={(e) => setPromoteForm((f) => ({ ...f, reason: e.target.value }))} />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setPromoteOpen(false)}>
              Cancel
            </Button>
            <Button
              disabled={!promoteForm.newDesignationId || promoteMut.isPending}
              onClick={() => promoteMut.mutate()}
            >
              Submit
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={transferOpen} onOpenChange={setTransferOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Transfer employee</DialogTitle>
            <DialogDescription>POST /employees/transfer</DialogDescription>
          </DialogHeader>
          <div className="grid gap-2 py-2">
            <FormLabel>New department</FormLabel>
            <select
              className={selectClassName}
              value={transferForm.newDepartmentId}
              onChange={(e) => setTransferForm((f) => ({ ...f, newDepartmentId: e.target.value }))}
            >
              <option value="">Select</option>
              {departments.map((d) => (
                <option key={d.departmentId} value={d.departmentId}>
                  {d.departmentName}
                </option>
              ))}
            </select>
            <FormLabel>New designation (optional)</FormLabel>
            <select
              className={selectClassName}
              value={transferForm.newDesignationId}
              onChange={(e) => setTransferForm((f) => ({ ...f, newDesignationId: e.target.value }))}
            >
              <option value="">Unchanged</option>
              {designations.map((d) => (
                <option key={d.designationId} value={d.designationId}>
                  {d.designationName}
                </option>
              ))}
            </select>
            <FormLabel>Effective date</FormLabel>
            <Input type="date" value={transferForm.effectiveDate} onChange={(e) => setTransferForm((f) => ({ ...f, effectiveDate: e.target.value }))} />
            <FormLabel>Reason</FormLabel>
            <Input value={transferForm.reason} onChange={(e) => setTransferForm((f) => ({ ...f, reason: e.target.value }))} />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setTransferOpen(false)}>
              Cancel
            </Button>
            <Button
              disabled={!transferForm.newDepartmentId || transferMut.isPending}
              onClick={() => transferMut.mutate()}
            >
              Submit
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

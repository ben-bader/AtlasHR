"use client"

import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog"
import { insuranceAPI } from "@/lib/api/insurance"
import type { AddInsurancePayload } from "@/lib/types"
import { Loader2 } from "lucide-react"
import { FormLabel } from "@/components/ui/form"

const empty: AddInsurancePayload = {
  employeeId: "",
  policyNumber: "",
  insuranceType: "",
  providerName: "",
}

export function InsuranceAdminView() {
  const qc = useQueryClient()
  const [employeeId, setEmployeeId] = useState("")
  const [runId, setRunId] = useState("")
  const [dialogOpen, setDialogOpen] = useState(false)
  const [form, setForm] = useState<AddInsurancePayload>(empty)

  const listQuery = useQuery({
    queryKey: ["insurances-employee", runId],
    queryFn: () => insuranceAPI.byEmployee(runId),
    enabled: runId.length > 0,
  })

  const addMut = useMutation({
    mutationFn: (payload: AddInsurancePayload) => insuranceAPI.add(payload),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ["insurances-employee"] })
      setDialogOpen(false)
      setForm(empty)
      if (variables.employeeId.trim()) setRunId(variables.employeeId.trim())
    },
  })

  const load = () => setRunId(employeeId.trim())

  const rows = listQuery.data ?? []

  return (
    <div className="space-y-6">
      <p className="text-sm text-muted-foreground">
        EmployeeInsuranceController — policies under /employees/insurances (proxied via gateway).
      </p>

      <div className="flex flex-wrap items-end gap-3 rounded-lg border border-border bg-background p-4">
        <div className="grid gap-1">
          <FormLabel>Employee ID</FormLabel>
          <Input value={employeeId} onChange={(e) => setEmployeeId(e.target.value)} placeholder="EMP..." className="w-56" />
        </div>
        <Button onClick={load}>Load policies</Button>
        <Button variant="secondary" onClick={() => setDialogOpen(true)}>
          Add policy
        </Button>
      </div>

      <div className="rounded-lg border border-border bg-background">
        {listQuery.isFetching ? (
          <div className="flex justify-center py-16">
            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Policy #</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Provider</TableHead>
                <TableHead>Coverage</TableHead>
                <TableHead>Period</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {runId.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-10 text-muted-foreground">
                    Load an employee to list insurances
                  </TableCell>
                </TableRow>
              ) : rows.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-10 text-muted-foreground">
                    No policies for this employee
                  </TableCell>
                </TableRow>
              ) : (
                rows.map((i) => (
                  <TableRow key={i.insuranceId}>
                    <TableCell className="font-mono text-xs">{i.policyNumber}</TableCell>
                    <TableCell>{i.insuranceType}</TableCell>
                    <TableCell>{i.providerName}</TableCell>
                    <TableCell>{i.coverageAmount ?? "-"}</TableCell>
                    <TableCell className="text-xs">
                      {i.policyStartDate} → {i.policyEndDate}
                    </TableCell>
                    <TableCell>{i.status || "-"}</TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        )}
      </div>

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="sm:max-w-lg max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Add insurance</DialogTitle>
            <DialogDescription>POST /employees/insurances · dates as YYYY-MM-DD</DialogDescription>
          </DialogHeader>
          <div className="grid gap-2 py-2">
            <FormLabel>Employee ID *</FormLabel>
            <Input value={form.employeeId} onChange={(e) => setForm((f) => ({ ...f, employeeId: e.target.value }))} />
            <FormLabel>Policy number</FormLabel>
            <Input value={form.policyNumber} onChange={(e) => setForm((f) => ({ ...f, policyNumber: e.target.value }))} />
            <FormLabel>Type</FormLabel>
            <Input value={form.insuranceType} onChange={(e) => setForm((f) => ({ ...f, insuranceType: e.target.value }))} />
            <FormLabel>Provider</FormLabel>
            <Input value={form.providerName} onChange={(e) => setForm((f) => ({ ...f, providerName: e.target.value }))} />
            <FormLabel>Coverage amount</FormLabel>
            <Input
              type="number"
              value={form.coverageAmount ?? ""}
              onChange={(e) =>
                setForm((f) => ({ ...f, coverageAmount: e.target.value ? Number(e.target.value) : undefined }))
              }
            />
            <FormLabel>Start date</FormLabel>
            <Input type="date" value={form.policyStartDate ?? ""} onChange={(e) => setForm((f) => ({ ...f, policyStartDate: e.target.value }))} />
            <FormLabel>End date</FormLabel>
            <Input type="date" value={form.policyEndDate ?? ""} onChange={(e) => setForm((f) => ({ ...f, policyEndDate: e.target.value }))} />
            <FormLabel>Premium</FormLabel>
            <Input
              type="number"
              value={form.premiumAmount ?? ""}
              onChange={(e) =>
                setForm((f) => ({ ...f, premiumAmount: e.target.value ? Number(e.target.value) : undefined }))
              }
            />
            <FormLabel>Beneficiary name</FormLabel>
            <Input value={form.beneficiaryName ?? ""} onChange={(e) => setForm((f) => ({ ...f, beneficiaryName: e.target.value }))} />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Cancel
            </Button>
            <Button disabled={!form.employeeId.trim() || addMut.isPending} onClick={() => addMut.mutate(form)}>
              Create
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

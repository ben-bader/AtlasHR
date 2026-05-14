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
import { departmentAPI } from "@/lib/api/department"
import type { CreateDepartmentPayload, Department } from "@/lib/types"
import { Loader2, Pencil, Trash2 } from "lucide-react"
import { FormLabel } from "@/components/ui/form"

const emptyForm: CreateDepartmentPayload = {
  departmentName: "",
  description: "",
  departmentCode: "",
  departmentHead: "",
}

export function DepartmentsAdminView() {
  const qc = useQueryClient()
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<Department | null>(null)
  const [form, setForm] = useState<CreateDepartmentPayload>(emptyForm)

  const { data = [], isLoading } = useQuery({
    queryKey: ["departments"],
    queryFn: () => departmentAPI.list(),
  })

  const createMut = useMutation({
    mutationFn: () => departmentAPI.create(form),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["departments"] })
      setDialogOpen(false)
      setForm(emptyForm)
    },
  })

  const updateMut = useMutation({
    mutationFn: () =>
      editing ? departmentAPI.update(editing.departmentId, form) : Promise.reject(),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["departments"] })
      setDialogOpen(false)
      setEditing(null)
      setForm(emptyForm)
    },
  })

  const deleteMut = useMutation({
    mutationFn: (id: number) => departmentAPI.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["departments"] }),
  })

  const openCreate = () => {
    setEditing(null)
    setForm(emptyForm)
    setDialogOpen(true)
  }

  const openEdit = (d: Department) => {
    setEditing(d)
    setForm({
      departmentName: d.departmentName,
      description: d.description ?? "",
      departmentCode: d.departmentCode ?? "",
      departmentHead: d.departmentHead ?? "",
      parentDepartmentId: d.parentDepartmentId ?? undefined,
    })
    setDialogOpen(true)
  }

  const submit = () => {
    if (!form.departmentName.trim()) return
    if (editing) updateMut.mutate()
    else createMut.mutate()
  }

  if (isLoading) {
    return (
      <div className="flex justify-center py-16">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <p className="text-sm text-muted-foreground">GET /departments · POST /departments · PUT /departments/:id</p>
        <Button onClick={openCreate}>Add department</Button>
      </div>

      <div className="rounded-lg border border-border bg-background">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Code</TableHead>
              <TableHead>Head</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-10 text-muted-foreground">
                  No departments
                </TableCell>
              </TableRow>
            ) : (
              data.map((d) => (
                <TableRow key={d.departmentId}>
                  <TableCell>{d.departmentId}</TableCell>
                  <TableCell className="font-medium">{d.departmentName}</TableCell>
                  <TableCell>{d.departmentCode || "-"}</TableCell>
                  <TableCell>{d.departmentHead || "-"}</TableCell>
                  <TableCell>{d.status || "-"}</TableCell>
                  <TableCell className="text-right space-x-2">
                    <Button variant="ghost" size="icon" onClick={() => openEdit(d)}>
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => {
                        if (confirm(`Delete department ${d.departmentName}?`)) deleteMut.mutate(d.departmentId)
                      }}
                    >
                      <Trash2 className="h-4 w-4 text-destructive" />
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>{editing ? "Edit department" : "Create department"}</DialogTitle>
            <DialogDescription>Maps to employee-service DepartmentController.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-3 py-2">
            <div className="grid gap-1">
              <FormLabel>Name *</FormLabel>
              <Input value={form.departmentName} onChange={(e) => setForm((f) => ({ ...f, departmentName: e.target.value }))} />
            </div>
            <div className="grid gap-1">
              <FormLabel>Code</FormLabel>
              <Input value={form.departmentCode} onChange={(e) => setForm((f) => ({ ...f, departmentCode: e.target.value }))} />
            </div>
            <div className="grid gap-1">
              <FormLabel>Description</FormLabel>
              <Input value={form.description} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))} />
            </div>
            <div className="grid gap-1">
              <FormLabel>Department head</FormLabel>
              <Input value={form.departmentHead} onChange={(e) => setForm((f) => ({ ...f, departmentHead: e.target.value }))} />
            </div>
            <div className="grid gap-1">
              <FormLabel>Parent department ID</FormLabel>
              <Input
                type="number"
                value={form.parentDepartmentId ?? ""}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    parentDepartmentId: e.target.value ? Number(e.target.value) : undefined,
                  }))
                }
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={submit} disabled={createMut.isPending || updateMut.isPending}>
              {editing ? "Save" : "Create"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

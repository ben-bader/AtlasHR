"use client"

import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
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
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/app/components/ui/dialog"
import {
  Alert,
  AlertDescription,
  AlertTitle,
} from "@/app/components/ui/alert"
import { departmentAPI } from "@/lib/api/department"
import type { CreateDepartmentPayload, Department } from "@/lib/types"
import { Loader2, Pencil, Trash2, AlertCircle, CheckCircle2 } from "lucide-react"
import { FormLabel } from "@/app/components/ui/form"

const emptyForm: CreateDepartmentPayload = {
  departmentName: "",
  description: "",
  departmentCode: "",
  departmentHead: "",
}

export function DepartmentsAdminView() {
  const qc = useQueryClient()
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false)
  const [deleteConfirm, setDeleteConfirm] = useState<{ id: number; name: string } | null>(null)
  const [editing, setEditing] = useState<Department | null>(null)
  const [form, setForm] = useState<CreateDepartmentPayload>(emptyForm)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const { data = [], isLoading } = useQuery({
    queryKey: ["departments"],
    queryFn: () => departmentAPI.list(),
  })

  const createMut = useMutation({
    mutationFn: () => departmentAPI.create(form),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["departments"] })
      setSuccessMessage("Department created successfully")
      setDialogOpen(false)
      setForm(emptyForm)
      setTimeout(() => setSuccessMessage(null), 3000)
    },
    onError: (error) => {
      console.error("Failed to create department:", error)
    },
  })

  const updateMut = useMutation({
    mutationFn: () =>
      editing ? departmentAPI.update(editing.departmentId, form) : Promise.reject(),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["departments"] })
      setSuccessMessage("Department updated successfully")
      setDialogOpen(false)
      setEditing(null)
      setForm(emptyForm)
      setTimeout(() => setSuccessMessage(null), 3000)
    },
    onError: (error) => {
      console.error("Failed to update department:", error)
    },
  })

  const deleteMut = useMutation({
    mutationFn: (id: number) => departmentAPI.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["departments"] })
      setSuccessMessage("Department deleted successfully")
      setTimeout(() => setSuccessMessage(null), 3000)
    },
    onError: (error) => {
      console.error("Failed to delete department:", error)
    },
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
      {successMessage && (
        <Alert className="border-green-600 bg-green-50">
          <CheckCircle2 className="h-4 w-4 text-green-600" />
          <AlertTitle className="text-green-900">Success</AlertTitle>
          <AlertDescription className="text-green-800">{successMessage}</AlertDescription>
        </Alert>
      )}
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
                        setDeleteConfirm({ id: d.departmentId, name: d.departmentName })
                        setDeleteConfirmOpen(true)
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
          {(createMut.isError || updateMut.isError) && (
            <Alert className="border-red-600 bg-red-50">
              <AlertCircle className="h-4 w-4 text-red-600" />
              <AlertTitle className="text-red-900">Error</AlertTitle>
              <AlertDescription className="text-red-800">
                {createMut.error instanceof Error ? createMut.error.message : 
                 updateMut.error instanceof Error ? updateMut.error.message :
                 `Failed to ${editing ? "update" : "create"} department. Please try again.`}
              </AlertDescription>
            </Alert>
          )}
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

      <Dialog open={deleteConfirmOpen} onOpenChange={setDeleteConfirmOpen}>
        <DialogContent className="sm:max-w-sm">
          <DialogHeader>
            <DialogTitle>Delete department</DialogTitle>
            <DialogDescription>This action cannot be undone.</DialogDescription>
          </DialogHeader>
          <div className="py-2">
            <p className="text-sm text-foreground">
              Are you sure you want to delete <span className="font-semibold">{deleteConfirm?.name}</span>?
            </p>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteConfirmOpen(false)}>
              Cancel
            </Button>
            <Button
              variant="destructive"
              disabled={deleteMut.isPending}
              onClick={() => {
                if (deleteConfirm) {
                  deleteMut.mutate(deleteConfirm.id, {
                    onSuccess: () => {
                      setDeleteConfirmOpen(false)
                      setDeleteConfirm(null)
                    },
                  })
                }
              }}
            >
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

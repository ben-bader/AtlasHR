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
import {
  Alert,
  AlertDescription,
  AlertTitle,
} from "@/components/ui/alert"
import { designationAPI } from "@/lib/api/designation"
import type { CreateDesignationPayload, Designation } from "@/lib/types"
import { Loader2, Pencil, Trash2, AlertCircle, CheckCircle2 } from "lucide-react"
import { FormLabel } from "@/components/ui/form"

const emptyForm: CreateDesignationPayload = {
  designationName: "",
  description: "",
  designationCode: "",
}

export function DesignationsAdminView() {
  const qc = useQueryClient()
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false)
  const [deleteConfirm, setDeleteConfirm] = useState<{ id: string; name: string } | null>(null)
  const [editing, setEditing] = useState<Designation | null>(null)
  const [form, setForm] = useState<CreateDesignationPayload>(emptyForm)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const { data = [], isLoading } = useQuery({
    queryKey: ["designations"],
    queryFn: () => designationAPI.list(),
  })

  const createMut = useMutation({
    mutationFn: () => designationAPI.create(form),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["designations"] })
      setSuccessMessage("Designation created successfully")
      setDialogOpen(false)
      setForm(emptyForm)
      setTimeout(() => setSuccessMessage(null), 3000)
    },
    onError: (error) => {
      console.error("Failed to create designation:", error)
    },
  })

  const updateMut = useMutation({
    mutationFn: () =>
      editing ? designationAPI.update(editing.designationId, form) : Promise.reject(),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["designations"] })
      setSuccessMessage("Designation updated successfully")
      setDialogOpen(false)
      setEditing(null)
      setForm(emptyForm)
      setTimeout(() => setSuccessMessage(null), 3000)
    },
    onError: (error) => {
      console.error("Failed to update designation:", error)
    },
  })

  const deleteMut = useMutation({
    mutationFn: (id: string) => designationAPI.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["designations"] })
      setSuccessMessage("Designation deleted successfully")
      setTimeout(() => setSuccessMessage(null), 3000)
    },
    onError: (error) => {
      console.error("Failed to delete designation:", error)
    },
  })

  const openCreate = () => {
    setEditing(null)
    setForm(emptyForm)
    setDialogOpen(true)
  }

  const openEdit = (d: Designation) => {
    setEditing(d)
    setForm({
      designationName: d.designationName,
      description: d.description ?? "",
      designationCode: d.designationCode ?? "",
      hierarchyLevel: d.hierarchyLevel,
      reportingDesignation: d.reportingDesignation,
    })
    setDialogOpen(true)
  }

  const submit = () => {
    if (!form.designationName.trim()) return
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
        <p className="text-sm text-muted-foreground">DesignationController · CRUD</p>
        <Button onClick={openCreate}>Add designation</Button>
      </div>

      <div className="rounded-lg border border-border bg-background">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Code</TableHead>
              <TableHead>Level</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-10 text-muted-foreground">
                  No designations
                </TableCell>
              </TableRow>
            ) : (
              data.map((d) => (
                <TableRow key={d.designationId}>
                  <TableCell className="font-mono text-xs">{d.designationId}</TableCell>
                  <TableCell className="font-medium">{d.designationName}</TableCell>
                  <TableCell>{d.designationCode || "-"}</TableCell>
                  <TableCell>{d.hierarchyLevel ?? "-"}</TableCell>
                  <TableCell>{d.status || "-"}</TableCell>
                  <TableCell className="text-right space-x-2">
                    <Button variant="ghost" size="icon" onClick={() => openEdit(d)}>
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => {
                        setDeleteConfirm({ id: d.designationId, name: d.designationName })
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
            <DialogTitle>{editing ? "Edit designation" : "Create designation"}</DialogTitle>
            <DialogDescription>Maps to CreateDesignationRequest.</DialogDescription>
          </DialogHeader>
          {(createMut.isError || updateMut.isError) && (
            <Alert className="border-red-600 bg-red-50">
              <AlertCircle className="h-4 w-4 text-red-600" />
              <AlertTitle className="text-red-900">Error</AlertTitle>
              <AlertDescription className="text-red-800">
                {createMut.error instanceof Error ? createMut.error.message : 
                 updateMut.error instanceof Error ? updateMut.error.message :
                 `Failed to ${editing ? "update" : "create"} designation. Please try again.`}
              </AlertDescription>
            </Alert>
          )}
          <div className="grid gap-3 py-2">
            <div className="grid gap-1">
              <FormLabel>Name *</FormLabel>
              <Input
                value={form.designationName}
                onChange={(e) => setForm((f) => ({ ...f, designationName: e.target.value }))}
              />
            </div>
            <div className="grid gap-1">
              <FormLabel>Code</FormLabel>
              <Input
                value={form.designationCode}
                onChange={(e) => setForm((f) => ({ ...f, designationCode: e.target.value }))}
              />
            </div>
            <div className="grid gap-1">
              <FormLabel>Description</FormLabel>
              <Input
                value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
              />
            </div>
            <div className="grid gap-1">
              <FormLabel>Hierarchy level</FormLabel>
              <Input
                type="number"
                value={form.hierarchyLevel ?? ""}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    hierarchyLevel: e.target.value ? Number(e.target.value) : undefined,
                  }))
                }
              />
            </div>
            <div className="grid gap-1">
              <FormLabel>Reporting designation id</FormLabel>
              <Input
                value={form.reportingDesignation ?? ""}
                onChange={(e) => setForm((f) => ({ ...f, reportingDesignation: e.target.value }))}
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
            <DialogTitle>Delete designation</DialogTitle>
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

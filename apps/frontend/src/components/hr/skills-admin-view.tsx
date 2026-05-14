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
import { skillsAPI } from "@/lib/api/skills"
import type { AddEmployeeSkillPayload } from "@/lib/types"
import { Loader2 } from "lucide-react"
import { FormLabel } from "@/components/ui/form"

export function SkillsAdminView() {
  const qc = useQueryClient()
  const [skillSearch, setSkillSearch] = useState("")
  const [runSearch, setRunSearch] = useState("")
  const [dialogOpen, setDialogOpen] = useState(false)
  const [form, setForm] = useState<AddEmployeeSkillPayload>({
    employeeId: "",
    skillName: "",
    competencyLevel: "",
    certification: "",
  })

  const searchQuery = useQuery({
    queryKey: ["skills-search", runSearch],
    queryFn: () => skillsAPI.bySkillName(runSearch),
    enabled: runSearch.length > 0,
  })

  const addMut = useMutation({
    mutationFn: (payload: AddEmployeeSkillPayload) => skillsAPI.add(payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["skills-search"] })
      setDialogOpen(false)
      setForm({ employeeId: "", skillName: "", competencyLevel: "", certification: "" })
    },
  })

  const rows = searchQuery.data ?? []

  return (
    <div className="space-y-8">
      <section className="space-y-3">
        <h2 className="text-lg font-semibold">Search by skill name</h2>
        <p className="text-sm text-muted-foreground">GET /skills/search/:skillName</p>
        <div className="flex flex-wrap gap-2">
          <Input
            value={skillSearch}
            onChange={(e) => setSkillSearch(e.target.value)}
            placeholder="Java, Leadership..."
            className="max-w-sm"
          />
          <Button onClick={() => setRunSearch(skillSearch.trim())}>Search</Button>
        </div>
        <div className="rounded-lg border border-border bg-background">
          {searchQuery.isFetching ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Employee</TableHead>
                  <TableHead>Skill</TableHead>
                  <TableHead>Level</TableHead>
                  <TableHead>Cert.</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {runSearch.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                      Enter a skill and search
                    </TableCell>
                  </TableRow>
                ) : rows.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                      No matches
                    </TableCell>
                  </TableRow>
                ) : (
                  rows.map((r) => (
                    <TableRow key={r.skillId}>
                      <TableCell className="font-mono text-xs">{r.employeeId}</TableCell>
                      <TableCell>{r.skillName}</TableCell>
                      <TableCell>{r.competencyLevel || "-"}</TableCell>
                      <TableCell>{r.certification || "-"}</TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          )}
        </div>
      </section>

      <section className="flex justify-between items-center">
        <div>
          <h2 className="text-lg font-semibold">Add skill</h2>
          <p className="text-sm text-muted-foreground">POST /skills</p>
        </div>
        <Button onClick={() => setDialogOpen(true)}>Add employee skill</Button>
      </section>

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add skill to employee</DialogTitle>
            <DialogDescription>AddEmployeeSkillRequest uses JSON field employeeId.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-3 py-2">
            <div className="grid gap-1">
              <FormLabel>Employee ID *</FormLabel>
              <Input value={form.employeeId} onChange={(e) => setForm((f) => ({ ...f, employeeId: e.target.value }))} />
            </div>
            <div className="grid gap-1">
              <FormLabel>Skill name *</FormLabel>
              <Input value={form.skillName} onChange={(e) => setForm((f) => ({ ...f, skillName: e.target.value }))} />
            </div>
            <div className="grid gap-1">
              <FormLabel>Competency level</FormLabel>
              <Input
                value={form.competencyLevel}
                onChange={(e) => setForm((f) => ({ ...f, competencyLevel: e.target.value }))}
              />
            </div>
            <div className="grid gap-1">
              <FormLabel>Certification</FormLabel>
              <Input value={form.certification} onChange={(e) => setForm((f) => ({ ...f, certification: e.target.value }))} />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Cancel
            </Button>
            <Button
              disabled={!form.employeeId.trim() || !form.skillName.trim() || addMut.isPending}
              onClick={() => addMut.mutate(form)}
            >
              Submit
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

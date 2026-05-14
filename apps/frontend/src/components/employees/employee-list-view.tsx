"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
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
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from "@/components/ui/dialog"
import { useEmployeeList, useTerminateEmployee } from "@/hooks/useEmployee"
import { Employee, EmployeeSearchParams } from "@/lib/types"
import { Trash2, Edit, Eye, Plus, Loader } from "lucide-react"

export function EmployeeListView() {
  const router = useRouter()
  const [searchParams, setSearchParams] = useState<EmployeeSearchParams>({
    page: 1,
    pageSize: 10,
  })
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null)
  const [showTerminateConfirm, setShowTerminateConfirm] = useState(false)
  const [lastWorkingDate, setLastWorkingDate] = useState("")
  const [terminationReason, setTerminationReason] = useState("")

  const { employees, isLoading, error, total, page, totalPages } =
    useEmployeeList(searchParams)
  const { terminateEmployee, isLoading: isTerminating } = useTerminateEmployee()

  const handleSearch = (query: string) => {
    setSearchParams((prev) => ({
      ...prev,
      search: query || undefined,
      page: 1,
    }))
  }

  const handleTerminate = async () => {
    if (!selectedEmployee || !lastWorkingDate) return

    try {
      await terminateEmployee({
        employeeId: selectedEmployee.id,
        lastWorkingDate,
        terminationReason: terminationReason || undefined,
      })
      setShowTerminateConfirm(false)
      setSelectedEmployee(null)
      setLastWorkingDate("")
      setTerminationReason("")
    } catch (err) {
      console.error("Failed to terminate employee:", err)
    }
  }

  const handlePageChange = (newPage: number) => {
    setSearchParams((prev) => ({
      ...prev,
      page: newPage,
    }))
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex-1 max-w-sm">
          <Input
            placeholder="Search employees..."
            onChange={(e) => handleSearch(e.target.value)}
            className="w-full"
          />
        </div>
        <Button
          onClick={() => router.push("/dashboard/employees/create")}
          className="flex items-center gap-2"
        >
          <Plus className="h-4 w-4" />
          Add Employee
        </Button>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded text-sm text-red-800">
          Failed to load employees. Please try again.
        </div>
      )}

      <div className="rounded-lg border border-border bg-background">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Department</TableHead>
              <TableHead>Position</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {employees.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-8 text-muted-foreground">
                  No employees found
                </TableCell>
              </TableRow>
            ) : (
              employees.map((employee) => (
                <TableRow key={employee.id}>
                  <TableCell className="font-medium">
                    {employee.firstName} {employee.lastName}
                  </TableCell>
                  <TableCell>{employee.email}</TableCell>
                  <TableCell>{employee.department || "-"}</TableCell>
                  <TableCell>{employee.designation || "-"}</TableCell>
                  <TableCell>
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      employee.status === "ACTIVE"
                        ? "bg-green-100 text-green-800"
                        : "bg-gray-100 text-gray-800"
                    }`}>
                      {employee.status || "ACTIVE"}
                    </span>
                  </TableCell>
                  <TableCell className="text-right space-x-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => router.push(`/dashboard/employees/${employee.id}`)}
                      title="View details"
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => router.push(`/dashboard/employees/${employee.id}/edit`)}
                      title="Edit"
                    >
                      <Edit className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => {
                        setSelectedEmployee(employee)
                        setShowTerminateConfirm(true)
                      }}
                      title="Terminate"
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

      {totalPages > 1 && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-muted-foreground">
            Page {page} of {totalPages} ({total} total employees)
          </p>
          <div className="flex gap-2">
            <Button
              variant="outline"
              onClick={() => handlePageChange(page - 1)}
              disabled={page === 1}
            >
              Previous
            </Button>
            <Button
              variant="outline"
              onClick={() => handlePageChange(page + 1)}
              disabled={page === totalPages}
            >
              Next
            </Button>
          </div>
        </div>
      )}

      <Dialog open={showTerminateConfirm} onOpenChange={setShowTerminateConfirm}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Terminate employee</DialogTitle>
            <DialogDescription>
              Posts to <span className="font-mono text-xs">POST /employees/terminate</span> for{" "}
              {selectedEmployee?.firstName} {selectedEmployee?.lastName}.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-2">
            <div className="grid gap-2">
              <label className="text-sm font-medium">Last working date *</label>
              <Input type="date" value={lastWorkingDate} onChange={(e) => setLastWorkingDate(e.target.value)} />
            </div>
            <div className="grid gap-2">
              <label className="text-sm font-medium">Reason</label>
              <Input
                placeholder="Resignation, restructuring..."
                value={terminationReason}
                onChange={(e) => setTerminationReason(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowTerminateConfirm(false)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={handleTerminate} disabled={isTerminating || !lastWorkingDate}>
              {isTerminating ? "Submitting..." : "Terminate"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

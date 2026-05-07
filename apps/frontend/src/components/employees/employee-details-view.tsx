"use client"

import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { useEmployee } from "@/hooks/useEmployee"
import { Loader, ArrowLeft, Edit } from "lucide-react"

interface EmployeeDetailsViewProps {
  employeeId: string
}

export function EmployeeDetailsView({ employeeId }: EmployeeDetailsViewProps) {
  const router = useRouter()
  const { employee, isLoading, error } = useEmployee(employeeId)

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
        <Button
          variant="outline"
          onClick={() => router.back()}
          className="flex items-center gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Back
        </Button>
        <div className="p-4 bg-red-50 border border-red-200 rounded text-sm text-red-800">
          Failed to load employee details. Please try again.
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">
            {employee.firstName} {employee.lastName}
          </h1>
          <p className="text-muted-foreground mt-1">{employee.designation || "Employee"}</p>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            onClick={() => router.back()}
            className="flex items-center gap-2"
          >
            <ArrowLeft className="h-4 w-4" />
            Back
          </Button>
          <Button
            onClick={() => router.push(`/dashboard/employees/${employeeId}/edit`)}
            className="flex items-center gap-2"
          >
            <Edit className="h-4 w-4" />
            Edit
          </Button>
        </div>
      </div>

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
            <p className="text-sm text-muted-foreground">Position</p>
            <p className="text-lg font-medium">{employee.designation || "-"}</p>
          </div>
        </div>

        <div className="space-y-4">
          <div>
            <p className="text-sm text-muted-foreground">Status</p>
            <p className={`text-lg font-medium inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
              employee.status === "ACTIVE"
                ? "bg-green-100 text-green-800"
                : "bg-gray-100 text-gray-800"
            }`}>
              {employee.status || "ACTIVE"}
            </p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Employment Type</p>
            <p className="text-lg font-medium">{employee.employmentType || "-"}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Join Date</p>
            <p className="text-lg font-medium">
              {employee.joinDate ? new Date(employee.joinDate).toLocaleDateString() : "-"}
            </p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground">Date of Birth</p>
            <p className="text-lg font-medium">
              {employee.dateOfBirth ? new Date(employee.dateOfBirth).toLocaleDateString() : "-"}
            </p>
          </div>
        </div>
      </div>

      {(employee.address || employee.city || employee.state || employee.zipCode || employee.country) && (
        <div className="pt-6 border-t border-border">
          <h2 className="text-xl font-semibold mb-4">Address</h2>
          <div className="space-y-2 text-sm">
            {employee.address && <p>{employee.address}</p>}
            {(employee.city || employee.state || employee.zipCode) && (
              <p>
                {employee.city && employee.city}
                {employee.city && employee.state && ", "}
                {employee.state && employee.state}
                {(employee.city || employee.state) && employee.zipCode && " "}
                {employee.zipCode && employee.zipCode}
              </p>
            )}
            {employee.country && <p>{employee.country}</p>}
          </div>
        </div>
      )}

      {(employee.createdAt || employee.updatedAt) && (
        <div className="pt-6 border-t border-border text-xs text-muted-foreground">
          {employee.createdAt && (
            <p>Created: {new Date(employee.createdAt).toLocaleString()}</p>
          )}
          {employee.updatedAt && (
            <p>Last Updated: {new Date(employee.updatedAt).toLocaleString()}</p>
          )}
        </div>
      )}
    </div>
  )
}

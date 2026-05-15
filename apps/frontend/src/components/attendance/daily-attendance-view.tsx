"use client"

import { useState } from "react"
import { useQuery } from "@tanstack/react-query"
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
import { attendanceAPI } from "@/lib/api/attendance"
import { Loader2 } from "lucide-react"

function formatDate(date?: string) {
  if (!date) return "-"
  return new Date(date).toLocaleDateString()
}

export function DailyAttendanceView() {
  const today = new Date().toISOString().slice(0, 10)
  const [selectedDate, setSelectedDate] = useState(today)

  const attendanceQuery = useQuery({
    queryKey: ["daily-attendance", selectedDate],
    queryFn: () => attendanceAPI.getDailyAttendanceByDate(selectedDate),
    enabled: false,
  })

  const handleLoad = () => {
    if (selectedDate) {
      attendanceQuery.refetch()
    }
  }

  const attendance = attendanceQuery.data
  const rows = attendance?.attendances ?? []

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold">Attendance</h1>
          <p className="text-sm text-muted-foreground mt-1">
            View daily attendance summaries and employee check-in/out details.
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          <div className="grid gap-1">
            <label className="text-sm font-medium">Pick date</label>
            <Input
              type="date"
              value={selectedDate}
              onChange={(event) => setSelectedDate(event.target.value)}
              className="w-48"
            />
          </div>
          <Button onClick={handleLoad} disabled={!selectedDate || attendanceQuery.isFetching}>
            Load attendance
          </Button>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-4">
        <div className="rounded-lg border border-border bg-background p-4">
          <p className="text-sm text-muted-foreground">Present</p>
          <p className="text-3xl font-semibold mt-2">{attendance?.totalPresent ?? "—"}</p>
        </div>
        <div className="rounded-lg border border-border bg-background p-4">
          <p className="text-sm text-muted-foreground">Absent</p>
          <p className="text-3xl font-semibold mt-2">{attendance?.totalAbsent ?? "—"}</p>
        </div>
        <div className="rounded-lg border border-border bg-background p-4">
          <p className="text-sm text-muted-foreground">Late</p>
          <p className="text-3xl font-semibold mt-2">{attendance?.totalLate ?? "—"}</p>
        </div>
        <div className="rounded-lg border border-border bg-background p-4">
          <p className="text-sm text-muted-foreground">On leave</p>
          <p className="text-3xl font-semibold mt-2">{attendance?.totalOnLeave ?? "—"}</p>
        </div>
      </div>

      <div className="rounded-lg border border-border bg-background overflow-x-auto">
        {attendanceQuery.isFetching ? (
          <div className="flex justify-center py-16">
            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Employee</TableHead>
                <TableHead>Date</TableHead>
                <TableHead>Check-in</TableHead>
                <TableHead>Check-out</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Hours</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {rows.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-10 text-muted-foreground">
                    {attendance ? "No attendance records found" : "Load attendance for a date"}
                  </TableCell>
                </TableRow>
              ) : (
                rows.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell className="font-mono text-xs">{item.employeeId}</TableCell>
                    <TableCell>{formatDate(item.date)}</TableCell>
                    <TableCell>{item.checkIn ? new Date(item.checkIn).toLocaleTimeString() : "—"}</TableCell>
                    <TableCell>{item.checkOut ? new Date(item.checkOut).toLocaleTimeString() : "—"}</TableCell>
                    <TableCell>{item.status ?? "—"}</TableCell>
                    <TableCell>{item.workedHours ?? "—"}</TableCell>
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

"use client";

import { Card } from "@/components/ui/card";
import { useEmployees } from "@/lib/hooks/useEmployees";

export default function DashboardPage() {
  const { data: employees, isLoading, isError } = useEmployees();

  return (
    <div className="space-y-6">
      <div className="rounded-[2rem] border border-slate-200 bg-white p-6 shadow-soft sm:p-8">
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.3em] text-slate-500">Dashboard</p>
            <h1 className="mt-2 text-3xl font-semibold text-slate-950">Team overview</h1>
          </div>
          <p className="rounded-full bg-slate-100 px-4 py-2 text-sm font-medium text-slate-700">HR service ready</p>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        <Card className="rounded-[2rem] border-slate-200 bg-white p-6">
          <p className="text-sm font-medium text-slate-500">Employees</p>
          <p className="mt-4 text-4xl font-semibold text-slate-950">
            {isLoading ? "..." : isError ? "—" : employees?.length ?? 0}
          </p>
          <p className="mt-3 text-sm text-slate-600">Count from the /api/employees endpoint.</p>
        </Card>

        <Card className="rounded-[2rem] border-slate-200 bg-white p-6">
          <p className="text-sm font-medium text-slate-500">Attendance</p>
          <p className="mt-4 text-4xl font-semibold text-slate-950">Ready</p>
          <p className="mt-3 text-sm text-slate-600">Replace placeholder data with attendance stats.</p>
        </Card>

        <Card className="rounded-[2rem] border-slate-200 bg-white p-6">
          <p className="text-sm font-medium text-slate-500">Payroll</p>
          <p className="mt-4 text-4xl font-semibold text-slate-950">Ready</p>
          <p className="mt-3 text-sm text-slate-600">Connect payroll microservice when available.</p>
        </Card>
      </div>
    </div>
  );
}

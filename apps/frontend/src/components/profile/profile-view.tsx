"use client"

import { Button } from "@/components/ui/button"
import { useAuth } from "@/hooks/useAuth"
import { Loader2 } from "lucide-react"

export function ProfileView() {
  const { user, isLoading, isAuthenticated, logout } = useAuth()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-80">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  if (!isAuthenticated || !user) {
    return (
      <div className="rounded-lg border border-border bg-background p-6 text-center">
        <p className="text-sm text-muted-foreground">You are not signed in.</p>
        <Button className="mt-4" onClick={() => logout()}>
          Sign out
        </Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold">Profile</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Account details and role information from the auth service.
          </p>
        </div>
        <Button variant="outline" onClick={() => logout()}>
          Logout
        </Button>
      </div>

      <div className="rounded-lg border border-border bg-background p-6 grid gap-4 sm:grid-cols-2">
        <div>
          <p className="text-sm text-muted-foreground">Username</p>
          <p className="text-lg font-semibold">{user.username}</p>
        </div>
        <div>
          <p className="text-sm text-muted-foreground">Email</p>
          <p className="text-lg font-semibold">{user.email ?? "-"}</p>
        </div>
        <div>
          <p className="text-sm text-muted-foreground">User ID</p>
          <p className="text-lg font-medium break-all">{user.id}</p>
        </div>
        <div>
          <p className="text-sm text-muted-foreground">Roles</p>
          <p className="text-lg font-semibold">{user.roles?.join(", ") ?? "User"}</p>
        </div>
      </div>
    </div>
  )
}

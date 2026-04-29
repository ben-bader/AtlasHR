"use client";

import { useMemo, useState } from "react";
import { Avatar } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { useAuth } from "@/lib/hooks/useAuth";

interface NavbarProps {
  onMobileMenuOpen: () => void;
}

export function Navbar({ onMobileMenuOpen }: NavbarProps) {
  const { user, logout } = useAuth();
  const displayName = useMemo(() => user?.username || "Guest", [user]);

  return (
    <header className="flex items-center justify-between gap-4 border-b border-slate-200 bg-white px-4 py-4 shadow-sm sm:px-6">
      <div className="flex items-center gap-3">
        <Button variant="ghost" className="lg:hidden" onClick={onMobileMenuOpen}>
          Menu
        </Button>
        <div>
          <p className="text-sm font-medium text-slate-900">Welcome back</p>
          <p className="text-xs text-slate-500">Secure HR workspace</p>
        </div>
      </div>
      <div className="flex items-center gap-3">
        <div className="hidden items-center gap-3 rounded-3xl bg-slate-50 px-4 py-3 sm:flex">
          <Avatar name={displayName} />
          <div className="text-sm">
            <p className="font-semibold text-slate-950">{displayName}</p>
            <p className="text-slate-500">HR Manager</p>
          </div>
        </div>
        <DropdownMenu>
          <DropdownMenuTrigger>Account</DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuItem onClick={logout}>Logout</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}

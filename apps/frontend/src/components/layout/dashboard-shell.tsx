"use client";

import { useState } from "react";
import { Sidebar } from "@/components/layout/sidebar";
import { Navbar } from "@/components/layout/navbar";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";

export function DashboardShell({ children }: { children: React.ReactNode }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="lg:flex lg:min-h-screen">
        <div className="hidden lg:block lg:w-72">
          <Sidebar />
        </div>

        <Sheet open={sidebarOpen} onOpenChange={setSidebarOpen}>
          <div className="lg:hidden">
            <SheetTrigger>
              <Button variant="ghost" className="m-4">Open menu</Button>
            </SheetTrigger>
            <SheetContent>
              <Sidebar />
            </SheetContent>
          </div>
        </Sheet>

        <div className="flex-1">
          <Navbar onMobileMenuOpen={() => setSidebarOpen(true)} />
          <main className="px-4 py-6 sm:px-6 lg:px-8">{children}</main>
        </div>
      </div>
    </div>
  );
}

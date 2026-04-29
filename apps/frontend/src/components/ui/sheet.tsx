"use client";

import * as React from "react";
import { cn } from "@/lib/utils/cn";

const SheetContext = React.createContext<{
  open: boolean;
  setOpen: (value: boolean) => void;
} | null>(null);

export function Sheet({ open, onOpenChange, children }: { open: boolean; onOpenChange: (open: boolean) => void; children: React.ReactNode }) {
  return <SheetContext.Provider value={{ open, setOpen: onOpenChange }}>{children}</SheetContext.Provider>;
}

export function SheetTrigger({ children }: { children: React.ReactNode }) {
  const context = React.useContext(SheetContext);
  if (!context) return null;

  return (
    <div onClick={() => context.setOpen(true)} className="inline-flex">
      {children}
    </div>
  );
}

export function SheetContent({ children }: { children: React.ReactNode }) {
  const context = React.useContext(SheetContext);
  if (!context) return null;

  return (
    <div
      className={cn(
        "fixed inset-0 z-50 flex bg-slate-950/40 p-4 backdrop-blur-sm",
        !context.open && "pointer-events-none opacity-0"
      )}
      aria-hidden={!context.open}
    >
      <div className="pointer-events-auto h-full w-full max-w-xs overflow-hidden rounded-[2rem] bg-white shadow-soft">
        {children}
      </div>
    </div>
  );
}

export function SheetClose({ children }: { children: React.ReactNode }) {
  const context = React.useContext(SheetContext);
  if (!context) return null;

  return (
    <button type="button" onClick={() => context.setOpen(false)} className="inline-flex">
      {children}
    </button>
  );
}

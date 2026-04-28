"use client";

import * as React from "react";
import { cn } from "@/lib/utils/cn";

type ToastMessage = {
  id: string;
  title: string;
  description?: string;
  variant?: "success" | "error" | "default";
};

type ToastContextValue = {
  toast: (message: Omit<ToastMessage, "id">) => void;
};

const ToastContext = React.createContext<ToastContextValue | undefined>(undefined);

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [messages, setMessages] = React.useState<ToastMessage[]>([]);

  const toast = React.useCallback((message: Omit<ToastMessage, "id">) => {
    const id = crypto.randomUUID();
    setMessages((current) => [{ id, ...message }, ...current]);
    window.setTimeout(() => {
      setMessages((current) => current.filter((item) => item.id !== id));
    }, 4200);
  }, []);

  return (
    <ToastContext.Provider value={{ toast }}>
      {children}
      <div className="fixed bottom-5 right-5 z-50 flex w-full max-w-sm flex-col gap-3">
        {messages.map(({ id, title, description, variant }) => (
          <div
            key={id}
            className={cn(
              "rounded-3xl border p-4 shadow-soft",
              variant === "success" && "border-emerald-200 bg-emerald-50 text-emerald-700",
              variant === "error" && "border-rose-200 bg-rose-50 text-rose-700",
              !variant && "border-slate-200 bg-white text-slate-900"
            )}
          >
            <p className="font-semibold">{title}</p>
            {description ? <p className="mt-1 text-sm leading-6 text-slate-600">{description}</p> : null}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = React.useContext(ToastContext);
  if (!context) {
    throw new Error("useToast must be used within a ToastProvider");
  }
  return context;
}

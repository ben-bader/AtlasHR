"use client"

import type { ReactNode } from "react"

import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/app/components/ui/breadcrumb"
import { SidebarTrigger } from "@/app/components/ui/sidebar"
import { Separator } from "@/app/components/ui/separator"

export type DashboardCrumb = { label: string; href?: string }

export function DashboardPageShell({
  crumbs,
  children,
}: {
  crumbs: DashboardCrumb[]
  children: ReactNode
}) {
  return (
    <>
      <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
        <div className="flex items-center gap-2 px-4 w-full">
          <SidebarTrigger className="-ml-1" />
          <Separator
            orientation="vertical"
            className="mr-2 data-vertical:h-4 data-vertical:self-auto"
          />
          <Breadcrumb>
            <BreadcrumbList>
              {crumbs.map((c, i) => {
                const isLast = i === crumbs.length - 1
                return (
                  <span key={`${c.label}-${i}`} className="contents">
                    {i > 0 ? <BreadcrumbSeparator className="hidden md:block" /> : null}
                    <BreadcrumbItem className={!isLast ? "hidden md:block" : undefined}>
                      {isLast || !c.href ? (
                        <BreadcrumbPage>{c.label}</BreadcrumbPage>
                      ) : (
                        <BreadcrumbLink href={c.href}>{c.label}</BreadcrumbLink>
                      )}
                    </BreadcrumbItem>
                  </span>
                )
              })}
            </BreadcrumbList>
          </Breadcrumb>
        </div>
      </header>
      <div className="flex flex-1 flex-col gap-4 p-4 pt-0">{children}</div>
    </>
  )
}

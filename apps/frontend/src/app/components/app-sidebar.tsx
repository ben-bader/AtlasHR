"use client"

import * as React from "react"

import { NavMain } from "@/app/components/nav-main"
import { NavUser } from "@/app/components/nav-user"
import { TeamSwitcher } from "@/app/components/team-switcher"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/app/components/ui/sidebar"
import {
  LayoutDashboard,
  Users,
  Building2,
  Award,
  Shield,
  GalleryVerticalEnd,
} from "lucide-react"

const data = {
  user: {
    name: "User",
    email: "user@atlashr.local",
    avatar: "/placeholder.svg",
  },
  teams: [
    {
      name: "AtlasHR",
      logo: <GalleryVerticalEnd className="size-4" />,
      plan: "Enterprise",
    },
  ],
  navMain: [
    {
      title: "Dashboard",
      url: "/dashboard",
      icon: <LayoutDashboard className="size-4" />,
    },
    {
      title: "Employees",
      url: "/dashboard/employees",
      icon: <Users className="size-4" />,
      items: [
        { title: "Directory", url: "/dashboard/employees" },
        { title: "Onboard", url: "/dashboard/employees/create" },
      ],
    },
    {
      title: "Organization",
      url: "/dashboard/departments",
      icon: <Building2 className="size-4" />,
      items: [
        { title: "Departments", url: "/dashboard/departments" },
        { title: "Designations", url: "/dashboard/designations" },
        { title: "Org chart", url: "/dashboard/organization" },
      ],
    },
    {
      title: "Skills",
      url: "/dashboard/skills",
      icon: <Award className="size-4" />,
    },
    {
      title: "Insurance",
      url: "/dashboard/insurance",
      icon: <Shield className="size-4" />,
    },
    {
      title: "Attendance",
      url: "/dashboard/attendance",
      icon: <Users className="size-4" />,
    },
    {
      title: "Profile",
      url: "/dashboard/profile",
      icon: <GalleryVerticalEnd className="size-4" />,
    },
  ],
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <TeamSwitcher teams={data.teams} />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={data.user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}

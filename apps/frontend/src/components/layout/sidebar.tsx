import { NavigationMenu, NavigationMenuItem } from "@/components/ui/navigation-menu";

const navigationItems = [
  { href: "/dashboard", title: "Dashboard", description: "Overview and metrics" },
  { href: "/dashboard/employees", title: "Employees", description: "Team directory and roles" },
  { href: "/dashboard/attendance", title: "Attendance", description: "Time and presence reports" },
  { href: "/dashboard/payroll", title: "Payroll", description: "Salary and compensation" },
  { href: "/dashboard/settings", title: "Settings", description: "Account and system settings" },
];

export function Sidebar() {
  return (
    <aside className="flex h-full flex-col gap-6 border-r border-slate-200 bg-white px-4 py-6 shadow-sm sm:px-6 lg:w-72">
      <div className="space-y-2">
        <p className="text-xs font-semibold uppercase tracking-[0.3em] text-slate-500">AtlasHR</p>
        <h2 className="text-xl font-semibold text-slate-950">HR Dashboard</h2>
      </div>
      <NavigationMenu>
        {navigationItems.map((item) => (
          <NavigationMenuItem key={item.href} href={item.href} title={item.title} description={item.description} />
        ))}
      </NavigationMenu>
    </aside>
  );
}

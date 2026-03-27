import {
  Search, History, BookOpen, Users, BarChart3, Settings,
  GraduationCap, Shield, Home
} from "lucide-react";
import { NavLink } from "@/components/NavLink";
import { useAuth } from "@/lib/auth-context";
import {
  Sidebar, SidebarContent, SidebarGroup, SidebarGroupContent,
  SidebarGroupLabel, SidebarMenu, SidebarMenuButton, SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";

export function DashboardSidebar() {
  const { user } = useAuth();
  const { state } = useSidebar();
  const collapsed = state === "collapsed";
  const role = user?.role;

  const commonItems = [
    { title: "Check Plagiarism", url: "/dashboard", icon: Search },
    { title: "History", url: "/dashboard/history", icon: History },
  ];

  const studentItems = [
    { title: "My Classes", url: "/dashboard/classes", icon: BookOpen },
  ];

  const lecturerItems = [
    { title: "Manage Classes", url: "/dashboard/classes", icon: BookOpen },
    { title: "Submissions", url: "/dashboard/submissions", icon: GraduationCap },
  ];

  const adminItems = [
    { title: "User Management", url: "/dashboard/users", icon: Users },
    { title: "Statistics", url: "/dashboard/stats", icon: BarChart3 },
    { title: "System Monitor", url: "/dashboard/monitor", icon: Settings },
  ];

  const roleItems =
    role === "admin" ? adminItems
    : role === "lecturer" ? lecturerItems
    : studentItems;

  return (
    <Sidebar collapsible="icon">
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>
            <Shield className="w-4 h-4 mr-1.5" />
            {!collapsed && "PlagGuard"}
          </SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem>
                <SidebarMenuButton asChild>
                  <NavLink to="/" end className="hover:bg-sidebar-accent/50" activeClassName="bg-sidebar-accent text-sidebar-primary font-medium">
                    <Home className="mr-2 h-4 w-4" />
                    {!collapsed && <span>Home</span>}
                  </NavLink>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
          <SidebarGroupLabel>{!collapsed && "General"}</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {commonItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <NavLink to={item.url} end className="hover:bg-sidebar-accent/50" activeClassName="bg-sidebar-accent text-sidebar-primary font-medium">
                      <item.icon className="mr-2 h-4 w-4" />
                      {!collapsed && <span>{item.title}</span>}
                    </NavLink>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
          <SidebarGroupLabel>{!collapsed && (role === "admin" ? "Administration" : role === "lecturer" ? "Teaching" : "Academic")}</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {roleItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <NavLink to={item.url} end className="hover:bg-sidebar-accent/50" activeClassName="bg-sidebar-accent text-sidebar-primary font-medium">
                      <item.icon className="mr-2 h-4 w-4" />
                      {!collapsed && <span>{item.title}</span>}
                    </NavLink>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}

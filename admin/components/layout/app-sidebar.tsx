"use client";

import { LogOut, Music2 } from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuBadge,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { NAV_GROUPS } from "@/config/navigation";
import { useAuth } from "@/hooks/use-auth";
import { useModuleRegistry } from "@/hooks/use-module-registry";
import { signOut } from "@/services/auth";

export function AppSidebar() {
  const pathname = usePathname();
  const { user, role } = useAuth();
  const { isNavModuleDisabled } = useModuleRegistry();

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader>
        <Link
          href="/"
          className="flex items-center gap-2 rounded-md px-2 py-1.5 hover:bg-sidebar-accent"
        >
          <Music2 className="size-5 text-primary" />
          <span className="font-heading text-sm font-bold tracking-tight group-data-[collapsible=icon]:hidden">
            MusicLinkd Ops
          </span>
        </Link>
      </SidebarHeader>
      <SidebarContent>
        {NAV_GROUPS.map((group) => (
          <SidebarGroup key={group.label}>
            <SidebarGroupLabel>{group.label}</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {group.items.map((navItem) => (
                  <SidebarMenuItem key={navItem.key}>
                    <SidebarMenuButton
                      render={<Link href={navItem.href} />}
                      isActive={pathname === navItem.href}
                      tooltip={navItem.label}
                    >
                      <navItem.icon />
                      <span>{navItem.label}</span>
                    </SidebarMenuButton>
                    {navItem.mock ? <SidebarMenuBadge>mock</SidebarMenuBadge> : null}
                    {isNavModuleDisabled(navItem.key) ? (
                      <SidebarMenuBadge className="text-destructive">disabled</SidebarMenuBadge>
                    ) : null}
                  </SidebarMenuItem>
                ))}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>
        ))}
      </SidebarContent>
      <SidebarFooter>
        <SidebarMenu>
          <SidebarMenuItem>
            <div className="flex items-center gap-2 px-2 py-1.5 text-xs text-sidebar-foreground/70 group-data-[collapsible=icon]:hidden">
              <span className="truncate">{user?.email}</span>
              <span className="ml-auto rounded bg-sidebar-accent px-1.5 py-0.5 font-medium">{role}</span>
            </div>
          </SidebarMenuItem>
          <SidebarMenuItem>
            <SidebarMenuButton onClick={() => signOut()} tooltip="Sign out">
              <LogOut />
              <span>Sign out</span>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
}

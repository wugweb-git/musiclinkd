import type { LucideIcon } from "lucide-react";
import {
  AudioLines,
  BadgeCheck,
  BarChart3,
  Bell,
  Calendar,
  Disc3,
  FileText,
  Flag,
  Gauge,
  LayoutDashboard,
  LayoutGrid,
  Map,
  MessageSquare,
  Newspaper,
  Palette,
  Power,
  ScrollText,
  Settings,
  ShieldCheck,
  ShoppingBag,
  Star,
  Tags,
  UserCog,
  Users,
} from "lucide-react";
import { MOCK_ONLY_MODULES, type ModuleKey } from "./constants";

export interface NavItem {
  key: ModuleKey;
  label: string;
  href: string;
  icon: LucideIcon;
  mock: boolean;
}

export interface NavGroup {
  label: string;
  items: NavItem[];
}

function item(key: ModuleKey, label: string, icon: LucideIcon): NavItem {
  return { key, label, href: `/${key}`, icon, mock: (MOCK_ONLY_MODULES as string[]).includes(key) };
}

export const NAV_GROUPS: NavGroup[] = [
  {
    label: "Overview",
    items: [item("dashboard", "Dashboard", LayoutDashboard)],
  },
  {
    label: "People",
    items: [
      item("users", "Users", Users),
      item("role-profiles", "Role Profiles", UserCog),
      item("studios", "Studios", Gauge),
    ],
  },
  {
    label: "Content",
    items: [
      item("marketplace", "Marketplace", ShoppingBag),
      item("projects", "Projects", Calendar),
      item("portfolio", "Portfolio", Disc3),
      item("reviews", "Reviews", Star),
      item("tracks", "Tracks", AudioLines),
    ],
  },
  {
    label: "Trust & safety",
    items: [
      item("verification", "Verification", BadgeCheck),
      item("moderation", "Moderation", Flag),
      item("audit-logs", "Audit logs", ScrollText),
    ],
  },
  {
    label: "Operations",
    items: [
      item("bookings", "Bookings", Calendar),
      item("messages", "Messages", MessageSquare),
      item("notifications", "Notifications", Bell),
      item("reports", "Reports & exports", FileText),
    ],
  },
  {
    label: "Platform",
    items: [
      item("pages", "Pages", Newspaper),
      item("analytics", "Analytics", BarChart3),
      item("master-data", "Master Data", Tags),
      item("modules", "Modules", Power),
      item("flows", "Flows", Map),
      item("capabilities", "Capabilities", ShieldCheck),
      item("widgets", "Widgets", LayoutGrid),
      item("brand", "Brand Assets", Palette),
      item("settings", "Settings", Settings),
    ],
  },
];

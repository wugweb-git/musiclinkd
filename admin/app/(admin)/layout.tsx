"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { AppSidebar } from "@/components/layout/app-sidebar";
import { LoadingScreen } from "@/components/shared/loading-screen";
import { SidebarInset, SidebarProvider } from "@/components/ui/sidebar";
import { useAuth } from "@/hooks/use-auth";

/**
 * Coarse gate shared by every module route: not-signed-in redirects to
 * /login here so 17 pages don't each duplicate that check. Per-module role
 * checks (which roles may see *this* module) still happen in each page via
 * useRequireModule — this layout only knows "is anyone home."
 */
export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!loading && !user) router.replace("/login");
  }, [loading, user, router]);

  if (loading || !user) return <LoadingScreen label="Checking your session…" />;

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>{children}</SidebarInset>
    </SidebarProvider>
  );
}

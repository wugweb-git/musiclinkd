"use client";

import { useEffect } from "react";
import { TooltipProvider } from "@/components/ui/tooltip";
import { AuthProvider } from "@/contexts/auth-context";
import { isFirebaseConfigured } from "@/config/environment";
import { initAppCheckIfConfigured } from "@/services/app-check";
import { SetupRequiredScreen } from "./shared/setup-required-screen";

export function Providers({ children }: { children: React.ReactNode }) {
  useEffect(() => {
    initAppCheckIfConfigured();
  }, []);

  if (!isFirebaseConfigured) {
    return <SetupRequiredScreen />;
  }

  return (
    <TooltipProvider>
      <AuthProvider>{children}</AuthProvider>
    </TooltipProvider>
  );
}

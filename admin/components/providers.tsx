"use client";

import { useEffect } from "react";
import ColorSchemeProvider from "gestalt/dist/contexts/ColorSchemeProvider";
import DefaultLabelProvider from "gestalt/dist/contexts/DefaultLabelProvider";
import { TooltipProvider } from "@/components/ui/tooltip";
import { AuthProvider } from "@/contexts/auth-context";
import { ModuleRegistryProvider } from "@/contexts/module-registry-context";
import { isFirebaseConfigured } from "@/config/environment";
import { initAppCheckIfConfigured } from "@/services/app-check";
import { SetupRequiredScreen } from "./shared/setup-required-screen";

// Gestalt (Pinterest's design system) is available in this app but not yet
// used anywhere — these providers only make its components importable.
// Nothing existing was changed. See frontend/guide/gestalt-library.md.
export function Providers({ children }: { children: React.ReactNode }) {
  useEffect(() => {
    initAppCheckIfConfigured();
  }, []);

  if (!isFirebaseConfigured) {
    return <SetupRequiredScreen />;
  }

  return (
    <ColorSchemeProvider colorScheme="userPreference">
      <DefaultLabelProvider>
        <TooltipProvider>
          <AuthProvider>
            <ModuleRegistryProvider>{children}</ModuleRegistryProvider>
          </AuthProvider>
        </TooltipProvider>
      </DefaultLabelProvider>
    </ColorSchemeProvider>
  );
}

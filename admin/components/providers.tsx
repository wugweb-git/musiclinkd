"use client";

import { useEffect } from "react";
// Imported from the package root, NOT "gestalt/dist/contexts/<Name>".
// Those deep paths resolve only at type-check time: dist/contexts/ ships
// .d.ts declaration files with no matching .js — the real implementations
// are all bundled into the single dist/gestalt.js. So tsc was happy while
// Turbopack failed with "Module not found" at build time. Both are real
// named exports of the root entry (verified in dist/index.d.ts), which
// resolves for both. Keep them here; don't "tidy" them back to deep paths.
import { ColorSchemeProvider, DefaultLabelProvider } from "gestalt";
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

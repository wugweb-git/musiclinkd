"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { MODULE_PERMISSIONS, NON_TOGGLEABLE_MODULES, type ModuleKey } from "@/config/constants";
import { useAuth } from "./use-auth";
import { useModuleRegistry } from "./use-module-registry";

/**
 * Call at the top of every module page. Redirects to /login if unauthenticated,
 * to /unauthorized if signed in but the current role isn't in that module's
 * allow-list, or to /module-disabled if an admin has toggled this module off
 * via the Modules registry. This is the actual enforcement point for module
 * disable — it fires on direct URL navigation, not just hidden sidebar links,
 * so a signed-in non-admin can't route around it. Returns the same shape as
 * useAuth so pages can also render a loading state without a second context read.
 */
export function useRequireModule(moduleKey: ModuleKey) {
  const auth = useAuth();
  const registry = useModuleRegistry();
  const router = useRouter();

  const isDisabled = !NON_TOGGLEABLE_MODULES.includes(moduleKey) && registry.isNavModuleDisabled(moduleKey);

  useEffect(() => {
    if (auth.loading) return;
    if (!auth.user) {
      router.replace(`/login?redirect=/${moduleKey}`);
      return;
    }
    if (!auth.role || !MODULE_PERMISSIONS[moduleKey].includes(auth.role)) {
      router.replace("/unauthorized");
      return;
    }
    if (!registry.loading && isDisabled) {
      router.replace(`/module-disabled?module=${moduleKey}`);
    }
  }, [auth.loading, auth.user, auth.role, registry.loading, isDisabled, moduleKey, router]);

  const hasAccess =
    !auth.loading &&
    !!auth.role &&
    MODULE_PERMISSIONS[moduleKey].includes(auth.role) &&
    !(!registry.loading && isDisabled);

  return { ...auth, hasAccess };
}

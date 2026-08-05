"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { MODULE_PERMISSIONS, type ModuleKey } from "@/config/constants";
import { useAuth } from "./use-auth";

/**
 * Call at the top of every module page. Redirects to /login if unauthenticated,
 * or to /unauthorized if signed in but the current role isn't in that module's
 * allow-list. Returns the same shape as useAuth so pages can also render a
 * loading state without a second context read.
 */
export function useRequireModule(moduleKey: ModuleKey) {
  const auth = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (auth.loading) return;
    if (!auth.user) {
      router.replace(`/login?redirect=/${moduleKey}`);
      return;
    }
    if (!auth.role || !MODULE_PERMISSIONS[moduleKey].includes(auth.role)) {
      router.replace("/unauthorized");
    }
  }, [auth.loading, auth.user, auth.role, moduleKey, router]);

  const hasAccess = !auth.loading && !!auth.role && MODULE_PERMISSIONS[moduleKey].includes(auth.role);

  return { ...auth, hasAccess };
}

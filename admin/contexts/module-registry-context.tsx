"use client";

import { createContext, useCallback, useEffect, useState } from "react";
import { isFirebaseConfigured } from "@/config/environment";
import { modulesRepo } from "@/services/repositories";
import type { ModuleRegistryDoc, WithId } from "@/types/firestore";

export interface ModuleRegistryContextValue {
  modules: WithId<ModuleRegistryDoc>[];
  loading: boolean;
  /** True only if a registry row exists for this admin ModuleKey and it's explicitly disabled. */
  isNavModuleDisabled: (moduleKey: string) => boolean;
  refetch: () => void;
}

export const ModuleRegistryContext = createContext<ModuleRegistryContextValue>({
  modules: [],
  loading: true,
  isNavModuleDisabled: () => false,
  refetch: () => {},
});

export function ModuleRegistryProvider({ children }: { children: React.ReactNode }) {
  const [modules, setModules] = useState<WithId<ModuleRegistryDoc>[]>([]);
  const [loading, setLoading] = useState(true);
  const [reloadKey, setReloadKey] = useState(0);

  const refetch = useCallback(() => setReloadKey((k) => k + 1), []);

  useEffect(() => {
    if (!isFirebaseConfigured) {
      setLoading(false);
      return;
    }
    let cancelled = false;
    modulesRepo
      .list()
      .then((rows) => {
        if (!cancelled) setModules(rows);
      })
      .catch(() => {
        // Registry not seeded yet or read failed — fail open (nothing disabled)
        // rather than locking admins out of every module.
        if (!cancelled) setModules([]);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [reloadKey]);

  function isNavModuleDisabled(moduleKey: string) {
    return modules.some((m) => m.data.navModuleKey === moduleKey && m.data.enabled === false);
  }

  return (
    <ModuleRegistryContext.Provider value={{ modules, loading, isNavModuleDisabled, refetch }}>
      {children}
    </ModuleRegistryContext.Provider>
  );
}

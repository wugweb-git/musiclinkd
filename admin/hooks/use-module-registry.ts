"use client";

import { useContext } from "react";
import { ModuleRegistryContext } from "@/contexts/module-registry-context";

export function useModuleRegistry() {
  return useContext(ModuleRegistryContext);
}

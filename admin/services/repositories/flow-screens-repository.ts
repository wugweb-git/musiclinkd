import { COLLECTIONS } from "@/config/constants";
import type { FlowScreenDoc } from "@/types/firestore";
import { createRepository } from "./generic-repository";

/**
 * Screens live in a subcollection under their flow (flows/{flowId}/screens),
 * so this repo is parametrized per flow — same pattern as
 * taxonomy-options-repository.ts.
 */
export function flowScreensRepo(flowId: string) {
  return createRepository<FlowScreenDoc>(`${COLLECTIONS.flows}/${flowId}/screens`);
}

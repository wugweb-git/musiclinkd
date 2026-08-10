import { COLLECTIONS } from "@/config/constants";
import type { TaxonomyOptionDoc } from "@/types/firestore";
import { createRepository } from "./generic-repository";

/**
 * Options live in a subcollection under their category
 * (taxonomy_categories/{categoryId}/options), so this repo is parametrized
 * per category rather than a single static export like the others in
 * ./index.ts.
 */
export function taxonomyOptionsRepo(categoryId: string) {
  return createRepository<TaxonomyOptionDoc>(`${COLLECTIONS.taxonomyCategories}/${categoryId}/options`);
}

"use client";

import { useEffect, useState } from "react";
import { taxonomyCategoriesRepo } from "@/services/repositories";
import { taxonomyOptionsRepo } from "@/services/repositories/taxonomy-options-repository";
import type { TaxonomyOptionDoc, WithId } from "@/types/firestore";

/**
 * Loads the active options for a master-data category. Returns null while
 * loading AND if the category hasn't been seeded yet — callers should treat
 * null as "fall back to freeform input," never as an error.
 */
export function useTaxonomyOptions(categoryKey: string): WithId<TaxonomyOptionDoc>[] | null {
  const [options, setOptions] = useState<WithId<TaxonomyOptionDoc>[] | null>(null);

  useEffect(() => {
    let cancelled = false;
    taxonomyCategoriesRepo
      .get(categoryKey)
      .then((category) => {
        if (cancelled) return;
        if (!category) {
          setOptions(null);
          return;
        }
        return taxonomyOptionsRepo(categoryKey)
          .list({ orderByField: "sortOrder", orderDirection: "asc" })
          .then((opts) => {
            if (!cancelled) setOptions(opts.filter((o) => o.data.isActive));
          });
      })
      .catch(() => {
        if (!cancelled) setOptions(null);
      });
    return () => {
      cancelled = true;
    };
  }, [categoryKey]);

  return options;
}

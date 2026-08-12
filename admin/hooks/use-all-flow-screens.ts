"use client";

import { useEffect, useState } from "react";
import { flowsRepo } from "@/services/repositories";
import { flowScreensRepo } from "@/services/repositories/flow-screens-repository";
import type { FlowDoc, FlowScreenDoc, WithId } from "@/types/firestore";

export interface FlowScreenOption {
  screenNumber: number;
  label: string;
  flowTitle: string;
}

/**
 * Flattens every flow's screens into one searchable list, for pickers that
 * need to reference a screen by its global screenNumber (e.g. widgets'
 * relatedScreens). No collection-group query exists on this schema, so this
 * does the N+1 fetch itself (flows, then each flow's screens subcollection)
 * — fine at this data volume (~24 flows, ~120 screens), not meant for
 * anything larger.
 */
export function useAllFlowScreens() {
  const [options, setOptions] = useState<FlowScreenOption[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const flows: WithId<FlowDoc>[] = await flowsRepo.list({ orderByField: "order", orderDirection: "asc" });
      const perFlow = await Promise.all(
        flows.map(async (flow) => {
          const screens: WithId<FlowScreenDoc>[] = await flowScreensRepo(flow.id).list({ orderByField: "order", orderDirection: "asc" });
          return screens
            .filter((s) => s.data.screenNumber !== undefined)
            .map((s) => ({
              screenNumber: s.data.screenNumber!,
              label: s.data.title,
              flowTitle: `Flow ${flow.data.order} — ${flow.data.title}`,
            }));
        }),
      );
      if (!cancelled) {
        setOptions(perFlow.flat().sort((a, b) => a.screenNumber - b.screenNumber));
        setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  return { options, loading };
}

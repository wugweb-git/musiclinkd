"use client";

import { useCallback, useEffect, useState } from "react";
import type { WithId } from "@/types/firestore";
import type { ListOptions } from "@/services/repositories/generic-repository";

interface Repo<T> {
  list: (options?: ListOptions) => Promise<WithId<T>[]>;
}

export function useCollection<T>(repo: Repo<T>, options?: ListOptions) {
  const [data, setData] = useState<WithId<T>[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);

  const refetch = useCallback(() => setReloadKey((k) => k + 1), []);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    repo
      .list(options)
      .then((rows) => {
        if (!cancelled) setData(rows);
      })
      .catch((err: unknown) => {
        if (!cancelled) setError(err instanceof Error ? err.message : "Failed to load data.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [reloadKey]);

  return { data, loading, error, refetch };
}

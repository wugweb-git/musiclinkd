import { Timestamp } from "firebase-admin/firestore";

/**
 * admin/types/firestore.ts types every Timestamp field against the *client*
 * SDK's `firebase/firestore` Timestamp class (correct for the app's own
 * browser code) — structurally close to, but not assignable from,
 * firebase-admin's Timestamp class (missing a `toJSON` method with an
 * identical signature). Both produce the exact same wire-format Firestore
 * timestamp; this is a type-checker-only mismatch. Cast through this alias
 * at the single point timestamps are constructed, rather than scattering
 * `as never` at every call site (the pattern the admin UI's own seed
 * buttons already use for the same reason — see flows/page.tsx).
 */
type FirestoreTimestamp = import("firebase/firestore").Timestamp;

export function slugify(value: string): string {
  return value
    .toLowerCase()
    .replace(/&/g, "and")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
}

export function mockId(prefix: string, name: string): string {
  return `mock-${prefix}-${slugify(name)}`;
}

/** 2026-08-12-relative date so seeded content reads as "recent" against today's date. */
export function daysAgo(days: number): FirestoreTimestamp {
  const d = new Date("2026-08-12T09:00:00Z");
  d.setUTCDate(d.getUTCDate() - days);
  return Timestamp.fromDate(d) as unknown as FirestoreTimestamp;
}

export function onDate(isoDate: string): FirestoreTimestamp {
  return Timestamp.fromDate(new Date(`${isoDate}T09:00:00Z`)) as unknown as FirestoreTimestamp;
}

/** Deterministic PRNG (mulberry32) — filler generation is reproducible across reruns, not Math.random(). */
export function makeRng(seed: number) {
  let a = seed;
  return function rng() {
    a |= 0;
    a = (a + 0x6d2b79f5) | 0;
    let t = Math.imul(a ^ (a >>> 15), 1 | a);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

export function pick<T>(rng: () => number, arr: readonly T[]): T {
  return arr[Math.floor(rng() * arr.length)]!;
}

export function pickN<T>(rng: () => number, arr: readonly T[], n: number): T[] {
  const pool = [...arr];
  const out: T[] = [];
  for (let i = 0; i < n && pool.length > 0; i++) {
    const idx = Math.floor(rng() * pool.length);
    out.push(pool.splice(idx, 1)[0]!);
  }
  return out;
}

export function intBetween(rng: () => number, min: number, max: number): number {
  return Math.floor(rng() * (max - min + 1)) + min;
}

/** Firestore rejects `undefined` field values outright — strip them recursively before every set(). */
export function stripUndefined<T>(value: T): T {
  if (Array.isArray(value)) {
    return value.map((v) => stripUndefined(v)) as unknown as T;
  }
  if (value !== null && typeof value === "object" && !(value as { toDate?: unknown }).toDate) {
    const out: Record<string, unknown> = {};
    for (const [k, v] of Object.entries(value as Record<string, unknown>)) {
      if (v !== undefined) out[k] = stripUndefined(v);
    }
    return out as T;
  }
  return value;
}

/**
 * Factory for a typed Firestore repository. Every module's data access goes
 * through one of these — no module component calls the Firestore SDK
 * directly. The 15 collections are structurally identical CRUD stores with
 * different query patterns, so one factory + per-collection instantiation
 * (see ./index.ts) beats 15 hand-written near-duplicates.
 */
import {
  type DocumentData,
  type Firestore,
  type QueryConstraint,
  collection,
  deleteDoc,
  doc,
  getCountFromServer,
  getDoc,
  getDocs,
  limit as fsLimit,
  orderBy,
  query,
  serverTimestamp,
  setDoc,
  startAfter,
  updateDoc,
  where,
} from "firebase/firestore";
import type { WithId } from "@/types/firestore";
import { getFirebaseDb } from "../firebase";

export interface ListOptions {
  orderByField?: string;
  orderDirection?: "asc" | "desc";
  whereClauses?: Array<[string, "==" | "!=" | "<" | "<=" | ">" | ">=", unknown]>;
  pageSize?: number;
  cursor?: DocumentData;
}

export function createRepository<T extends object>(collectionName: string) {
  const col = (db: Firestore) => collection(db, collectionName);

  return {
    /**
     * Fetches one batch (default 200 recent records) that the caller's
     * DataTable then sorts/searches/paginates client-side. This is a
     * deliberate simplification, not hidden: it's "recent N records"
     * browsing, not true cursor-based infinite pagination over the whole
     * collection. Fine for an internal admin tool at this data volume;
     * revisit with real server-side cursor paging if any collection grows
     * past what's comfortable to pull in one batch.
     */
    async list(options: ListOptions = {}): Promise<WithId<T>[]> {
      const db = getFirebaseDb();
      const constraints: QueryConstraint[] = [];
      for (const [field, op, value] of options.whereClauses ?? []) {
        constraints.push(where(field, op, value));
      }
      if (options.orderByField) {
        constraints.push(orderBy(options.orderByField, options.orderDirection ?? "desc"));
      }
      if (options.cursor) constraints.push(startAfter(options.cursor));
      constraints.push(fsLimit(options.pageSize ?? 200));

      const snap = await getDocs(query(col(db), ...constraints));
      return snap.docs.map((d) => ({ id: d.id, data: d.data() as T }));
    },

    async count(whereClauses: ListOptions["whereClauses"] = []): Promise<number> {
      const db = getFirebaseDb();
      const constraints: QueryConstraint[] = whereClauses.map(([field, op, value]) => where(field, op, value));
      const snap = await getCountFromServer(query(col(db), ...constraints));
      return snap.data().count;
    },

    async get(id: string): Promise<WithId<T> | null> {
      const snap = await getDoc(doc(getFirebaseDb(), collectionName, id));
      if (!snap.exists()) return null;
      return { id: snap.id, data: snap.data() as T };
    },

    async create(id: string, data: T): Promise<void> {
      await setDoc(doc(getFirebaseDb(), collectionName, id), data);
    },

    async update(id: string, data: Partial<T>): Promise<void> {
      await updateDoc(doc(getFirebaseDb(), collectionName, id), data as DocumentData);
    },

    async remove(id: string): Promise<void> {
      await deleteDoc(doc(getFirebaseDb(), collectionName, id));
    },

    serverTimestamp,
  };
}

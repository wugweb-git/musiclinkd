"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { PageShell } from "@/components/layout/page-shell";
import type { ModuleKey } from "@/config/constants";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import type { ListOptions } from "@/services/repositories/generic-repository";
import type { WithId } from "@/types/firestore";
import { DataTable } from "./data-table";

interface Repo<T> {
  list: (options?: ListOptions) => Promise<WithId<T>[]>;
}

interface CollectionListPageProps<T> {
  moduleKey: ModuleKey;
  title: string;
  description: string;
  repo: Repo<T>;
  columns: ColumnDef<WithId<T>>[];
  listOptions?: ListOptions;
  searchPlaceholder?: string;
  emptyTitle?: string;
  emptyDescription?: string;
  mock?: boolean;
  actions?: React.ReactNode;
  bulkActions?: (rows: WithId<T>[], refetch: () => void) => React.ReactNode;
  onRowClick?: (row: WithId<T>) => void;
}

export function CollectionListPage<T>({
  moduleKey,
  title,
  description,
  repo,
  columns,
  listOptions,
  searchPlaceholder,
  emptyTitle,
  emptyDescription,
  mock,
  actions,
  bulkActions,
  onRowClick,
}: CollectionListPageProps<T>) {
  const { hasAccess } = useRequireModule(moduleKey);
  const { data, loading, error, refetch } = useCollection(repo, listOptions);

  if (!hasAccess) return null;

  return (
    <PageShell title={title} description={description} actions={actions} mock={mock}>
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder={searchPlaceholder}
        emptyTitle={emptyTitle}
        emptyDescription={emptyDescription}
        bulkActions={bulkActions ? (rows) => bulkActions(rows, refetch) : undefined}
        onRowClick={onRowClick}
        meta={{ refetch }}
      />
    </PageShell>
  );
}

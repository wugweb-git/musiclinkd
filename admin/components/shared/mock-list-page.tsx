"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { PageShell } from "@/components/layout/page-shell";
import type { ModuleKey } from "@/config/constants";
import { useRequireModule } from "@/hooks/use-require-module";
import { DataTable } from "./data-table";

interface MockListPageProps<T> {
  moduleKey: ModuleKey;
  title: string;
  description: string;
  data: T[];
  columns: ColumnDef<T>[];
  searchPlaceholder?: string;
}

export function MockListPage<T>({ moduleKey, title, description, data, columns, searchPlaceholder }: MockListPageProps<T>) {
  const { hasAccess } = useRequireModule(moduleKey);
  if (!hasAccess) return null;

  return (
    <PageShell title={title} description={description} mock>
      <DataTable columns={columns} data={data} searchPlaceholder={searchPlaceholder} />
    </PageShell>
  );
}

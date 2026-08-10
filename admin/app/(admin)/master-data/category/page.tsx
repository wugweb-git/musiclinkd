"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { ArrowLeft, Plus, Trash2 } from "lucide-react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense, useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { taxonomyOptionsRepo } from "@/services/repositories/taxonomy-options-repository";
import type { TaxonomyOptionDoc, WithId } from "@/types/firestore";
import { OptionEditorSheet } from "./option-editor-sheet";

type Row = WithId<TaxonomyOptionDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.label", header: "Label" },
  { accessorKey: "data.value", header: "Value" },
  { accessorKey: "data.sortOrder", header: "Sort" },
  {
    accessorKey: "data.isActive",
    header: "Status",
    cell: ({ row }) =>
      row.original.data.isActive ? (
        <Badge variant="secondary">Active</Badge>
      ) : (
        <Badge variant="outline">Inactive</Badge>
      ),
  },
];

function CategoryDetail() {
  const searchParams = useSearchParams();
  const categoryId = searchParams.get("id") ?? "";
  const { hasAccess } = useRequireModule("masterData");
  const repo = taxonomyOptionsRepo(categoryId);
  const { data, loading, error, refetch } = useCollection(repo, {
    orderByField: "sortOrder",
    orderDirection: "asc",
  });
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;
  if (!categoryId) {
    return (
      <PageShell title="Master Data" description="No category selected.">
        <Link href="/master-data" className="text-sm text-primary underline">
          Back to categories
        </Link>
      </PageShell>
    );
  }

  const withActions: ColumnDef<Row>[] = [
    ...columns,
    {
      id: "actions",
      header: "",
      cell: ({ row, table }) => {
        const option = row.original;
        const tableRefetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
        return (
          <div className="flex justify-end" data-no-row-click>
            <ConfirmDialog
              trigger={
                <Button size="icon-sm" variant="destructive">
                  <Trash2 className="size-3.5" />
                </Button>
              }
              title={`Delete "${option.data.label}"?`}
              description="This removes the option from the picker immediately."
              destructive
              confirmLabel="Delete"
              onConfirm={async () => {
                await repo.remove(option.id);
                toast.success("Option deleted");
                tableRefetch?.();
              }}
            />
          </div>
        );
      },
    },
  ];

  return (
    <PageShell
      title={`Master Data — ${categoryId}`}
      description="Options in this category, in the order they'll be offered."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New option
        </Button>
      }
    >
      <Link href="/master-data" className="inline-flex w-fit items-center gap-1 text-sm text-primary underline">
        <ArrowLeft className="size-3.5" /> Back to categories
      </Link>
      <DataTable
        columns={withActions}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search options..."
        emptyTitle="No options yet"
        emptyDescription="Add options for this category."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <OptionEditorSheet
        categoryId={categoryId}
        option={selected}
        open={editorOpen}
        onOpenChange={setEditorOpen}
        onSaved={refetch}
        nextSortOrder={data.length}
      />
    </PageShell>
  );
}

export default function CategoryDetailPage() {
  return (
    <Suspense fallback={null}>
      <CategoryDetail />
    </Suspense>
  );
}

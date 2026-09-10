"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Plus, Trash2 } from "lucide-react";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { TruncatedCell } from "@/components/shared/truncated-cell";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { taxonomyCategoriesRepo } from "@/services/repositories";
import type { TaxonomyCategoryDoc, WithId } from "@/types/firestore";
import { CategoryEditorSheet } from "./category-editor-sheet";

type Row = WithId<TaxonomyCategoryDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "id", header: "Key" },
  { accessorKey: "data.label", header: "Label" },
  {
    accessorKey: "data.description",
    header: "Description",
    cell: ({ row }) => <TruncatedCell text={row.original.data.description} maxWidth={360} />,
  },
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
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const category = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="icon-sm" variant="destructive">
                <Trash2 className="size-3.5" />
              </Button>
            }
            title={`Delete "${category.data.label}"?`}
            description="This does not delete the options underneath it — remove those first if you want a clean removal."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await taxonomyCategoriesRepo.remove(category.id);
              toast.success("Category deleted");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function MasterDataPage() {
  const { hasAccess } = useRequireModule("master-data");
  const router = useRouter();
  const { data, loading, error, refetch } = useCollection(taxonomyCategoriesRepo);
  const [editorOpen, setEditorOpen] = useState(false);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Master Data"
      description="Controlled reference lists (genres, and future categories) that editor sheets across the admin panel — and eventually the app — pull their options from, instead of freeform text or hardcoded lists."
      actions={
        <Button size="sm" onClick={() => setEditorOpen(true)}>
          <Plus className="size-4" /> New category
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search categories..."
        emptyTitle="No categories yet"
        emptyDescription='Create a category (e.g. "genres") to start managing its options.'
        onRowClick={(row) => router.push(`/master-data/category?id=${row.id}`)}
        meta={{ refetch }}
      />

      <CategoryEditorSheet open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}

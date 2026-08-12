"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Pencil, Plus, Trash2 } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { brandAssetsRepo } from "@/services/repositories";
import type { BrandAssetsDoc, WithId } from "@/types/firestore";
import { BrandAssetEditorSheet } from "./brand-asset-editor-sheet";

type Row = WithId<BrandAssetsDoc>;

function assetCount(data: BrandAssetsDoc): number {
  return [data.logoUrl, data.iconUrl, data.thumbnailUrl, data.heroBannerUrl, data.darkVariantUrl, data.lightVariantUrl].filter(Boolean).length;
}

function buildColumns(onEdit: (row: Row) => void): ColumnDef<Row>[] {
  return [
    {
      id: "preview",
      header: "",
      cell: ({ row }) => (
        <Avatar className="size-8 rounded-md">
          <AvatarImage src={row.original.data.logoUrl ?? row.original.data.iconUrl} alt="" className="object-contain" />
          <AvatarFallback className="rounded-md text-xs">{row.original.data.ownerUid.slice(0, 2).toUpperCase()}</AvatarFallback>
        </Avatar>
      ),
    },
    { accessorKey: "data.ownerUid", header: "Owner UID" },
    {
      id: "assets",
      header: "Assets set",
      cell: ({ row }) => `${assetCount(row.original.data)} / 6`,
    },
    {
      id: "actions",
      header: "",
      cell: ({ row, table }) => {
        const asset = row.original;
        const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
        return (
          <div className="flex justify-end gap-1" data-no-row-click>
            <Button size="icon-sm" variant="outline" onClick={() => onEdit(asset)}>
              <Pencil className="size-3.5" />
            </Button>
            <ConfirmDialog
              trigger={
                <Button size="icon-sm" variant="destructive">
                  <Trash2 className="size-3.5" />
                </Button>
              }
              title="Delete this brand asset set?"
              description="Removes the logo/icon/banner URLs on record for this owner — does not delete the underlying files in storage."
              destructive
              confirmLabel="Delete"
              onConfirm={async () => {
                await brandAssetsRepo.remove(asset.id);
                toast.success("Brand assets deleted");
                refetch?.();
              }}
            />
          </div>
        );
      },
    },
  ];
}

export default function BrandAssetsPage() {
  const { hasAccess } = useRequireModule("brand");
  const { data, loading, error, refetch } = useCollection(brandAssetsRepo);
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  const columns = buildColumns((row) => {
    setSelected(row);
    setEditorOpen(true);
  });

  return (
    <PageShell
      title="Brand Assets"
      description="Logo/icon/banner URLs for Business-family profiles (studios, labels, agencies) — public-read, editable here or by the owner. No confirmed app-side writer yet, so entries here are the source of truth until that's wired up."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New asset set
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search by owner UID..."
        emptyTitle="No brand assets yet"
        emptyDescription="Add a logo/icon/banner set for a business profile."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <BrandAssetEditorSheet asset={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}

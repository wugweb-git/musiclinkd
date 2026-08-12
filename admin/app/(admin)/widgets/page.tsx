"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { CircleHelp, Eye, EyeOff, Pencil, Plus, Sparkles, Trash2 } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { widgetsRepo } from "@/services/repositories";
import type { WidgetDoc, WithId } from "@/types/firestore";
import { DEFAULT_WIDGETS } from "./default-widgets";
import { WidgetEditorSheet } from "./widget-editor-sheet";

type Row = WithId<WidgetDoc>;

function VisibilityBadge({ isVisible }: { isVisible?: boolean }) {
  if (isVisible === undefined) {
    return (
      <Badge variant="outline" className="gap-1 text-muted-foreground">
        <CircleHelp className="size-3.5" /> Unconfirmed
      </Badge>
    );
  }
  return isVisible ? (
    <Badge variant="default" className="gap-1">
      <Eye className="size-3.5" /> Visible
    </Badge>
  ) : (
    <Badge variant="destructive" className="gap-1">
      <EyeOff className="size-3.5" /> Hidden
    </Badge>
  );
}

function buildColumns(onEdit: (row: Row) => void): ColumnDef<Row>[] {
  return [
    { accessorKey: "data.order", header: "#" },
    { accessorKey: "data.name", header: "Widget" },
    { accessorKey: "data.category", header: "Category" },
    { accessorKey: "data.codeMapping", header: "Code mapping" },
    {
      id: "relatedScreens",
      header: "Screens",
      cell: ({ row }) => {
        const count = row.original.data.relatedScreens?.length ?? 0;
        return count > 0 ? <Badge variant="outline">{count}</Badge> : <span className="text-muted-foreground">—</span>;
      },
    },
    {
      id: "visibility",
      header: "Visibility",
      cell: ({ row }) => <VisibilityBadge isVisible={row.original.data.isVisible} />,
    },
    {
      id: "actions",
      header: "",
      cell: ({ row, table }) => {
        const widget = row.original;
        const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
        return (
          <div className="flex justify-end gap-1" data-no-row-click>
            <Button size="icon-sm" variant="outline" onClick={() => onEdit(widget)}>
              <Pencil className="size-3.5" />
            </Button>
            <ConfirmDialog
              trigger={
                <Button size="icon-sm" variant="destructive">
                  <Trash2 className="size-3.5" />
                </Button>
              }
              title={`Delete "${widget.data.name}"?`}
              description="Removes this widget from the catalog."
              destructive
              confirmLabel="Delete"
              onConfirm={async () => {
                await widgetsRepo.remove(widget.id);
                toast.success("Widget deleted");
                refetch?.();
              }}
            />
          </div>
        );
      },
    },
  ];
}

export default function WidgetsPage() {
  const { hasAccess } = useRequireModule("widgets");
  const { data, loading, error, refetch } = useCollection(widgetsRepo, { orderByField: "order", orderDirection: "asc" });
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);
  const [seeding, setSeeding] = useState(false);

  if (!hasAccess) return null;

  const columns = buildColumns((row) => {
    setSelected(row);
    setEditorOpen(true);
  });

  async function handleSeed() {
    setSeeding(true);
    try {
      const now = widgetsRepo.serverTimestamp();
      let created = 0;
      for (const w of DEFAULT_WIDGETS) {
        const existing = await widgetsRepo.get(w.key);
        if (existing) continue;
        await widgetsRepo.create(w.key, { ...w, createdAt: now as never, updatedAt: now as never });
        created += 1;
      }
      toast.success(created ? `Seeded ${created} default widget(s)` : "All default widgets already exist");
      refetch();
    } catch {
      toast.error("Failed to seed default widgets");
    } finally {
      setSeeding(false);
    }
  }

  return (
    <PageShell
      title="Widgets"
      description="The app's UI-component catalog as data — what each card/widget shows and which real component it maps to. Visibility is tracked separately since several widgets are wired up but deliberately hidden, or documented from an audit that couldn't confirm every call site."
      actions={
        <div className="flex gap-2">
          <Button size="sm" variant="outline" onClick={handleSeed} disabled={seeding}>
            <Sparkles className="size-4" /> {seeding ? "Seeding…" : "Seed defaults"}
          </Button>
          <Button
            size="sm"
            onClick={() => {
              setSelected(null);
              setEditorOpen(true);
            }}
          >
            <Plus className="size-4" /> New widget
          </Button>
        </div>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search widgets..."
        emptyTitle="No widgets yet"
        emptyDescription='Click "Seed defaults" to populate the 27-widget starter catalog, or add one manually.'
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <WidgetEditorSheet
        widget={selected}
        open={editorOpen}
        onOpenChange={setEditorOpen}
        onSaved={refetch}
        nextOrder={data.length + 1}
      />
    </PageShell>
  );
}

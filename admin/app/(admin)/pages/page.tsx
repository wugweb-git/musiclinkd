"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Plus } from "lucide-react";
import { useState } from "react";
import { PageShell } from "@/components/layout/page-shell";
import { DataTable } from "@/components/shared/data-table";
import { StatusBadge } from "@/components/shared/status-badge";
import { Button } from "@/components/ui/button";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { pageContentRepo } from "@/services/repositories";
import type { PageContentDoc, WithId } from "@/types/firestore";
import { PageEditorSheet } from "./page-editor-sheet";

type Row = WithId<PageContentDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "id", header: "Slug" },
  { accessorKey: "data.route", header: "Route" },
  { accessorKey: "data.title", header: "Title" },
  {
    accessorKey: "data.status",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
  },
];

/**
 * Seed slugs matching website/'s existing page folders — create these first
 * via "New page" so the homepage and footer pages all have a record here.
 */
const SUGGESTED_SLUGS = ["home", "pricing", "contact", "legal-privacy", "legal-terms", "legal-cookies"];

export default function PagesPage() {
  const { hasAccess } = useRequireModule("pages");
  const { data, loading, error, refetch } = useCollection(pageContentRepo);
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Page Management"
      description={`Homepage, CMS pages, and footer pages editable here. Suggested slugs to seed first: ${SUGGESTED_SLUGS.join(", ")}.`}
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New page
        </Button>
      }
    >
      <div className="rounded-lg border border-dashed border-amber-500/50 bg-amber-500/10 px-4 py-2 text-sm text-amber-700 dark:text-amber-400">
        <span className="font-medium">Not yet live:</span> pages edited here are stored in Firestore
        only. <code className="text-xs">frontend/website/</code> is 100% static HTML and doesn&apos;t
        read this collection — a save here doesn&apos;t change what&apos;s live at musiclinkd.com until a
        build-time content pipeline is added (see{" "}
        <code className="text-xs">admin/docs/architecture/CMS_Content_Mapping.md</code>).
      </div>
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search pages..."
        emptyTitle="No pages yet"
        emptyDescription="Create the homepage and footer pages to start managing website content."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
      />

      <PageEditorSheet
        page={selected}
        open={editorOpen}
        onOpenChange={setEditorOpen}
        onSaved={refetch}
      />
    </PageShell>
  );
}

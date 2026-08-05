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
const SUGGESTED_SLUGS = ["home", "about", "pricing", "careers", "blog", "legal-privacy", "legal-terms", "legal-cookies"];

export default function PagesPage() {
  const { hasAccess } = useRequireModule("pages");
  const { data, loading, error, refetch } = useCollection(pageContentRepo);
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Page Management"
      description={`Homepage, CMS pages, and footer pages editable here. website/ is still static HTML and doesn't read this collection yet — see the schema doc's limitation note. Suggested slugs to seed first: ${SUGGESTED_SLUGS.join(", ")}.`}
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

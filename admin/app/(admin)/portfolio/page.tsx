"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { portfolioProjectsRepo } from "@/services/repositories";
import type { PortfolioProjectDoc, WithId } from "@/types/firestore";

type Row = WithId<PortfolioProjectDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.title", header: "Title" },
  { accessorKey: "data.userId", header: "Owner" },
  {
    accessorKey: "data.projectType",
    header: "Type",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.projectType}</Badge>,
  },
  { accessorKey: "data.genre", header: "Genre" },
  { accessorKey: "data.releaseDate", header: "Release date" },
  {
    accessorKey: "data.collaboratorUids",
    header: "Collaborators",
    cell: ({ row }) => row.original.data.collaboratorUids?.length ?? 0,
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const project = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end" data-no-row-click>
          <ConfirmDialog
            trigger={
              <Button size="icon-sm" variant="destructive">
                <Trash2 className="size-3.5" />
              </Button>
            }
            title={`Remove "${project.data.title}"?`}
            description="Removes this portfolio project permanently — use for spam or abusive uploads."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await portfolioProjectsRepo.remove(project.id);
              toast.success("Project removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function PortfolioPage() {
  return (
    <CollectionListPage
      moduleKey="portfolio"
      title="Portfolio"
      description="User-published portfolio projects (albums, singles, EPs, scores, beat tapes). No confirmed app-side writer yet as of this session's audits — this module is ready to receive data the moment publishing is wired up. See admin/docs/architecture/Edge_Functions_And_Errors.md."
      repo={portfolioProjectsRepo}
      columns={columns}
      listOptions={{ orderByField: "createdAt", orderDirection: "desc" }}
      searchPlaceholder="Search portfolio projects..."
      emptyTitle="No portfolio projects yet"
      emptyDescription="Published projects will show up here once the app writes to portfolio_projects."
    />
  );
}

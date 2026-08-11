"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { CollectionListPage } from "@/components/shared/collection-list-page";
import { StatusBadge } from "@/components/shared/status-badge";
import { projectsRepo } from "@/services/repositories";
import type { ProjectDoc, WithId } from "@/types/firestore";

type Row = WithId<ProjectDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.title", header: "Project" },
  { accessorKey: "data.creatorUid", header: "Creator" },
  { accessorKey: "data.roleRequired", header: "Role needed" },
  { accessorKey: "data.budget", header: "Budget" },
  { accessorKey: "data.location", header: "Location", cell: ({ row }) => row.original.data.location ?? "—" },
  {
    accessorKey: "data.applicantCount",
    header: "Applicants",
    cell: ({ row }) => row.original.data.applicantCount ?? 0,
  },
  {
    accessorKey: "data.status",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
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
            description="Removes this project/collaboration call permanently — use for spam or abusive listings."
            destructive
            confirmLabel="Delete"
            onConfirm={async () => {
              await projectsRepo.remove(project.id);
              toast.success("Project removed");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function ProjectsPage() {
  return (
    <CollectionListPage
      moduleKey="projects"
      title="Projects"
      description="Open collaboration calls and project opportunities."
      repo={projectsRepo}
      columns={columns}
      listOptions={{ orderByField: "createdAt", orderDirection: "desc" }}
      searchPlaceholder="Search projects..."
      emptyTitle="No projects yet"
      emptyDescription="Open project calls will show up here."
    />
  );
}

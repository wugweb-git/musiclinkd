"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { Ban, BadgeCheck, Plus } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/shared/confirm-dialog";
import { DataTable } from "@/components/shared/data-table";
import { PageShell } from "@/components/layout/page-shell";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { usersRepo } from "@/services/repositories";
import type { UserDoc, WithId } from "@/types/firestore";
import { UserEditorSheet } from "./user-editor-sheet";

type Row = WithId<UserDoc>;

const columns: ColumnDef<Row>[] = [
  {
    accessorKey: "data.displayName",
    header: "User",
    cell: ({ row }) => (
      <div className="flex items-center gap-2">
        <Avatar className="size-7">
          <AvatarImage src={row.original.data.avatarUrl} alt="" />
          <AvatarFallback>{row.original.data.displayName?.[0] ?? "?"}</AvatarFallback>
        </Avatar>
        <div>
          <p className="font-medium leading-none">{row.original.data.displayName}</p>
          <p className="text-xs text-muted-foreground">{row.original.data.email}</p>
        </div>
      </div>
    ),
  },
  {
    accessorKey: "data.role",
    header: "Role",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.role}</Badge>,
  },
  { accessorKey: "data.location", header: "Location" },
  {
    accessorKey: "data.isVerified",
    header: "Verified",
    cell: ({ row }) => (row.original.data.isVerified ? <BadgeCheck className="size-4 text-primary" /> : "—"),
  },
  {
    accessorKey: "data.isBanned",
    header: "Status",
    cell: ({ row }) => (row.original.data.isBanned ? <Badge variant="destructive">Banned</Badge> : <Badge variant="secondary">Active</Badge>),
  },
  {
    id: "actions",
    header: "",
    cell: ({ row, table }) => {
      const user = row.original;
      const refetch = (table.options.meta as { refetch?: () => void } | undefined)?.refetch;
      return (
        <div className="flex justify-end gap-1" data-no-row-click>
          {!user.data.isVerified && (
            <ConfirmDialog
              trigger={
                <Button variant="outline" size="sm">
                  Verify
                </Button>
              }
              title={`Verify ${user.data.displayName}?`}
              description="This marks the profile as verified. Only admins/moderators can do this."
              onConfirm={async () => {
                await usersRepo.update(user.id, { isVerified: true });
                toast.success("User verified");
                refetch?.();
              }}
            />
          )}
          <ConfirmDialog
            trigger={
              <Button variant={user.data.isBanned ? "outline" : "destructive"} size="sm">
                {user.data.isBanned ? "Unban" : <Ban className="size-3.5" />}
              </Button>
            }
            title={user.data.isBanned ? `Unban ${user.data.displayName}?` : `Ban ${user.data.displayName}?`}
            description={
              user.data.isBanned
                ? "Restores this user's access to the platform."
                : "Blocks this user from the platform immediately."
            }
            destructive={!user.data.isBanned}
            onConfirm={async () => {
              await usersRepo.update(user.id, { isBanned: !user.data.isBanned });
              toast.success(user.data.isBanned ? "User unbanned" : "User banned");
              refetch?.();
            }}
          />
        </div>
      );
    },
  },
];

export default function UsersPage() {
  const { hasAccess } = useRequireModule("users");
  const { data, loading, error, refetch } = useCollection(usersRepo, {
    orderByField: "createdAt",
    orderDirection: "desc",
  });
  const [editorOpen, setEditorOpen] = useState(false);
  const [selected, setSelected] = useState<Row | null>(null);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Users"
      description="Artists, producers, vocalists, studios, and labels registered on MusicLinkd."
      actions={
        <Button
          size="sm"
          onClick={() => {
            setSelected(null);
            setEditorOpen(true);
          }}
        >
          <Plus className="size-4" /> New user
        </Button>
      }
    >
      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search users..."
        emptyTitle="No users yet"
        emptyDescription="Registered users will show up here."
        onRowClick={(row) => {
          setSelected(row);
          setEditorOpen(true);
        }}
        meta={{ refetch }}
      />

      <UserEditorSheet user={selected} open={editorOpen} onOpenChange={setEditorOpen} onSaved={refetch} />
    </PageShell>
  );
}

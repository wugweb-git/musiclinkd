"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { PageShell } from "@/components/layout/page-shell";
import { DataTable } from "@/components/shared/data-table";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ADMIN_ROLES, FEATURE_FLAGS } from "@/config/constants";
import { useAuth } from "@/hooks/use-auth";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { activateRemoteConfig, getFlagBoolean, getFlagValue } from "@/services/remote-config";
import { adminRolesRepo } from "@/services/repositories";
import type { AdminRole, AdminRoleDoc, WithId } from "@/types/firestore";

type Row = WithId<AdminRoleDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "id", header: "UID" },
  {
    accessorKey: "data.role",
    header: "Role",
    cell: ({ row }) => <Badge variant="outline">{row.original.data.role}</Badge>,
  },
  { accessorKey: "data.grantedBy", header: "Granted by" },
];

export default function SettingsPage() {
  const { hasAccess } = useRequireModule("settings");
  const { user } = useAuth();
  const { data, loading, error, refetch } = useCollection(adminRolesRepo);
  const [flags, setFlags] = useState<{ maintenance: boolean; minVersion: string; supportEmail: string } | null>(
    null,
  );
  const [newUid, setNewUid] = useState("");
  const [newRole, setNewRole] = useState<AdminRole>("ROLE_SUPPORT");
  const [granting, setGranting] = useState(false);

  useEffect(() => {
    if (!hasAccess) return;
    activateRemoteConfig()
      .then(() => {
        setFlags({
          maintenance: getFlagBoolean(FEATURE_FLAGS.maintenanceMode),
          minVersion: getFlagValue(FEATURE_FLAGS.minimumAppVersion),
          supportEmail: getFlagValue(FEATURE_FLAGS.supportContactEmail),
        });
      })
      .catch(() => setFlags(null));
  }, [hasAccess]);

  if (!hasAccess) return null;

  async function handleGrant() {
    if (!newUid || !user) return;
    setGranting(true);
    try {
      await adminRolesRepo.create(newUid, {
        role: newRole,
        grantedBy: user.uid,
        grantedAt: adminRolesRepo.serverTimestamp() as never,
      });
      toast.success("Role granted");
      setNewUid("");
      refetch();
    } catch {
      toast.error("Failed to grant role — check the UID and your own permissions.");
    } finally {
      setGranting(false);
    }
  }

  return (
    <PageShell title="Settings" description="Feature flags and admin role management.">
      <Card>
        <CardHeader>
          <CardTitle>Feature flags</CardTitle>
          <CardDescription>
            Read from Remote Config (remoteconfig.template.json). Edit values in the Firebase console —
            this panel doesn&apos;t write flags yet.
          </CardDescription>
        </CardHeader>
        <CardContent className="grid grid-cols-1 gap-3 sm:grid-cols-3">
          <div>
            <p className="text-xs text-muted-foreground">Maintenance mode</p>
            <p className="font-medium">{flags ? String(flags.maintenance) : "…"}</p>
          </div>
          <div>
            <p className="text-xs text-muted-foreground">Minimum app version</p>
            <p className="font-medium">{flags?.minVersion ?? "…"}</p>
          </div>
          <div>
            <p className="text-xs text-muted-foreground">Support email</p>
            <p className="font-medium">{flags?.supportEmail ?? "…"}</p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Grant admin access</CardTitle>
          <CardDescription>
            Writes to /admin_roles — rules only allow an existing ROLE_ADMIN to do this, so this form
            fails for anyone else regardless of what the UI shows.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-3 sm:flex-row sm:items-end">
          <div className="flex-1 space-y-1.5">
            <Label htmlFor="grant-uid">Firebase Auth UID</Label>
            <Input id="grant-uid" value={newUid} onChange={(e) => setNewUid(e.target.value)} placeholder="abc123..." />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="grant-role">Role</Label>
            <Select value={newRole} onValueChange={(v) => setNewRole(v as AdminRole)}>
              <SelectTrigger id="grant-role" className="w-40">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {ADMIN_ROLES.map((r) => (
                  <SelectItem key={r} value={r}>
                    {r}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
        <CardFooter>
          <Button onClick={handleGrant} disabled={granting || !newUid}>
            {granting ? "Granting…" : "Grant role"}
          </Button>
        </CardFooter>
      </Card>

      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search admins..."
        emptyTitle="No admins yet"
        emptyDescription="Granted roles will show up here."
      />
    </PageShell>
  );
}

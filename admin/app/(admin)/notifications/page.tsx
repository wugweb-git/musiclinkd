"use client";

import type { ColumnDef } from "@tanstack/react-table";
import { useState } from "react";
import { toast } from "sonner";
import { PageShell } from "@/components/layout/page-shell";
import { DataTable } from "@/components/shared/data-table";
import { StatusBadge } from "@/components/shared/status-badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { ANALYTICS_EVENTS } from "@/config/constants";
import { useAuth } from "@/hooks/use-auth";
import { useCollection } from "@/hooks/use-collection";
import { useRequireModule } from "@/hooks/use-require-module";
import { trackEvent } from "@/services/analytics";
import { broadcastMessagesRepo } from "@/services/repositories";
import type { BroadcastMessageDoc, WithId } from "@/types/firestore";

type Row = WithId<BroadcastMessageDoc>;

const columns: ColumnDef<Row>[] = [
  { accessorKey: "data.title", header: "Title" },
  { accessorKey: "data.audience", header: "Audience" },
  { accessorKey: "data.sentBy", header: "Sent by" },
  {
    accessorKey: "data.status",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.data.status} />,
  },
];

export default function NotificationsPage() {
  const { hasAccess } = useRequireModule("notifications");
  const { user } = useAuth();
  const { data, loading, error, refetch } = useCollection(broadcastMessagesRepo);
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [audience, setAudience] = useState("ALL");
  const [sending, setSending] = useState(false);

  if (!hasAccess) return null;

  async function handleSend() {
    if (!title || !body || !user) return;
    setSending(true);
    try {
      const id = crypto.randomUUID();
      await broadcastMessagesRepo.create(id, {
        title,
        body,
        audience,
        sentBy: user.uid,
        sentAt: broadcastMessagesRepo.serverTimestamp() as never,
        status: "QUEUED",
      });
      await trackEvent(ANALYTICS_EVENTS.broadcastSent, { audience });
      toast.success("Broadcast queued — actual delivery needs a Cloud Function to send via FCM (not built yet).");
      setTitle("");
      setBody("");
      refetch();
    } catch {
      toast.error("Failed to queue broadcast");
    } finally {
      setSending(false);
    }
  }

  return (
    <PageShell title="Notifications" description="Send FCM push broadcasts to users.">
      <Card>
        <CardHeader>
          <CardTitle>Compose broadcast</CardTitle>
          <CardDescription>
            This writes a queued record to Firestore. It does not send the push notification yet —
            that requires a Cloud Function triggered off this collection, which is out of scope for a
            static-export admin panel with no backend of its own.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-3">
          <div className="space-y-1.5">
            <Label htmlFor="broadcast-title">Title</Label>
            <Input id="broadcast-title" value={title} onChange={(e) => setTitle(e.target.value)} />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="broadcast-body">Message</Label>
            <Textarea id="broadcast-body" value={body} onChange={(e) => setBody(e.target.value)} rows={3} />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="broadcast-audience">Audience</Label>
            <Input id="broadcast-audience" value={audience} onChange={(e) => setAudience(e.target.value)} />
          </div>
        </CardContent>
        <CardFooter>
          <Button onClick={handleSend} disabled={sending || !title || !body}>
            {sending ? "Queuing…" : "Queue broadcast"}
          </Button>
        </CardFooter>
      </Card>

      <DataTable
        columns={columns}
        data={data}
        loading={loading}
        error={error}
        searchPlaceholder="Search broadcasts..."
        emptyTitle="No broadcasts yet"
        emptyDescription="Sent broadcasts will show up here."
      />
    </PageShell>
  );
}

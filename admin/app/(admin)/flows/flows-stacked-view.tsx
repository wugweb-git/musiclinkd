"use client";

import { ChevronDown, ChevronRight } from "lucide-react";
import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { flowScreensRepo } from "@/services/repositories/flow-screens-repository";
import type { FlowDoc, FlowScreenDoc, FlowStatus, WithId } from "@/types/firestore";

const STATUS_VARIANT: Record<FlowStatus, "default" | "secondary" | "destructive" | "outline"> = {
  documented: "default",
  partial: "secondary",
  "app-mocked": "outline",
  missing: "destructive",
};

function screenLabel(screen: WithId<FlowScreenDoc>, allScreens: WithId<FlowScreenDoc>[]): string {
  if (screen.data.screenNumber !== undefined) return String(screen.data.screenNumber);
  if (screen.data.variantOf !== undefined) {
    const siblings = allScreens.filter((s) => s.data.variantOf === screen.data.variantOf).sort((a, b) => a.data.order - b.data.order);
    const position = siblings.findIndex((s) => s.id === screen.id) + 1;
    return `${screen.data.variantOf}-${position || 1}`;
  }
  return "—";
}

function FlowGroup({ flow, onFlowClick }: { flow: WithId<FlowDoc>; onFlowClick: (id: string) => void }) {
  const [expanded, setExpanded] = useState(false);
  const [screens, setScreens] = useState<WithId<FlowScreenDoc>[] | null>(null);
  const [loadingScreens, setLoadingScreens] = useState(false);

  async function toggle() {
    if (!expanded && screens === null) {
      setLoadingScreens(true);
      const loaded = await flowScreensRepo(flow.id).list({ orderByField: "order", orderDirection: "asc" });
      setScreens(loaded);
      setLoadingScreens(false);
    }
    setExpanded((e) => !e);
  }

  return (
    <Card className={expanded ? "shadow-raised-top" : undefined}>
      <CardHeader
        className="cursor-pointer select-none flex-row items-center justify-between space-y-0"
        onClick={toggle}
      >
        <div className="flex items-center gap-2">
          {expanded ? <ChevronDown className="size-4 text-muted-foreground" /> : <ChevronRight className="size-4 text-muted-foreground" />}
          <span className="font-mono text-sm text-muted-foreground">Flow {flow.data.order}</span>
          <button
            type="button"
            className="font-medium underline-offset-2 hover:underline"
            onClick={(e) => {
              e.stopPropagation();
              onFlowClick(flow.id);
            }}
          >
            {flow.data.title}
          </button>
        </div>
        <div className="flex items-center gap-2">
          {screens !== null ? <Badge variant="outline">{screens.length} screen{screens.length === 1 ? "" : "s"}</Badge> : null}
          <Badge variant={STATUS_VARIANT[flow.data.status]}>{flow.data.status}</Badge>
        </div>
      </CardHeader>
      {expanded ? (
        <CardContent className="border-t pt-3">
          {loadingScreens ? (
            <div className="space-y-2">
              <Skeleton className="h-5 w-full" />
              <Skeleton className="h-5 w-full" />
            </div>
          ) : screens && screens.length > 0 ? (
            <ul className="space-y-1.5">
              {screens.map((s) => (
                <li key={s.id} className="flex items-center gap-2 text-sm">
                  <span className="w-12 shrink-0 font-mono text-muted-foreground">{screenLabel(s, screens)}</span>
                  <span>{s.data.title}</span>
                  {s.data.variantLabel ? (
                    <Badge variant="outline" className="text-xs">
                      {s.data.variantLabel}
                    </Badge>
                  ) : null}
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-sm text-muted-foreground">No screens seeded yet for this flow.</p>
          )}
        </CardContent>
      ) : null}
    </Card>
  );
}

export function FlowsStackedView({
  flows,
  loading,
  onFlowClick,
}: {
  flows: WithId<FlowDoc>[];
  loading?: boolean;
  onFlowClick: (id: string) => void;
}) {
  if (loading) {
    return (
      <div className="space-y-2">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton key={i} className="h-14 w-full" />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-2">
      {flows.map((flow) => (
        <FlowGroup key={flow.id} flow={flow} onFlowClick={onFlowClick} />
      ))}
    </div>
  );
}

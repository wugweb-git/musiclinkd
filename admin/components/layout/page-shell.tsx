import { SiteHeader } from "./site-header";

interface PageShellProps {
  title: string;
  description?: string;
  actions?: React.ReactNode;
  mock?: boolean;
  children: React.ReactNode;
}

export function PageShell({ title, description, actions, mock, children }: PageShellProps) {
  return (
    <div className="flex flex-1 flex-col">
      <SiteHeader title={title} />
      <div className="flex flex-1 flex-col gap-5 p-4 md:p-6">
        {mock ? (
          <div className="rounded-lg border border-dashed border-primary/40 bg-primary/5 px-4 py-2 text-sm text-primary">
            This module has no backing Firestore collection yet — showing seed data only. See the
            approved plan&apos;s flagged open decisions for what needs to happen before this is real.
          </div>
        ) : null}
        {(description || actions) && (
          <div className="flex flex-wrap items-start justify-between gap-4">
            {description ? (
              <p className="max-w-2xl text-sm leading-relaxed text-muted-foreground">{description}</p>
            ) : (
              <div />
            )}
            {actions ? <div className="flex shrink-0 items-center gap-2">{actions}</div> : null}
          </div>
        )}
        {children}
      </div>
    </div>
  );
}

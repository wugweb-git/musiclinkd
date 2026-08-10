"use client";

import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import type { TaxonomyOptionDoc, WithId } from "@/types/firestore";

interface CappedCheckboxListProps {
  label: string;
  options: WithId<TaxonomyOptionDoc>[];
  selected: string[];
  onToggle: (value: string) => void;
  /** Omit for an uncapped multi-select. */
  max?: number;
}

/**
 * Shared UI for every master-data-backed multi-select field (genres, roles,
 * languages, instruments, persona badges, ...) in the Users editor. Surfaces
 * "legacy" selected values that don't match any current option (pre-taxonomy
 * data, or an option that's since been deactivated/renamed) as removable
 * chips instead of silently hiding them — they still count toward `max`.
 */
export function CappedCheckboxList({ label, options, selected, onToggle, max }: CappedCheckboxListProps) {
  const knownValues = new Set(options.map((o) => o.data.value));
  const legacy = selected.filter((v) => !knownValues.has(v));

  return (
    <div className="space-y-1.5">
      <Label>
        {label}
        {max ? ` (up to ${max} — ${selected.length}/${max} selected)` : ""}
      </Label>
      <div className="flex flex-wrap gap-x-4 gap-y-2 rounded-md border p-3">
        {options.map((opt) => {
          const checked = selected.includes(opt.data.value);
          const disabled = !checked && !!max && selected.length >= max;
          return (
            <label key={opt.id} className={`flex items-center gap-2 text-sm ${disabled ? "opacity-40" : ""}`}>
              <Checkbox checked={checked} disabled={disabled} onCheckedChange={() => onToggle(opt.data.value)} />
              {opt.data.label}
            </label>
          );
        })}
        {!options.length ? <p className="text-xs text-muted-foreground">No active options yet.</p> : null}
      </div>
      {legacy.length ? (
        <div className="rounded-md border border-dashed border-amber-500/50 bg-amber-500/10 p-2 text-xs text-amber-700 dark:text-amber-400">
          <p className="mb-1 font-medium">
            {legacy.length} stored value{legacy.length > 1 ? "s" : ""} not in the current list
            {max ? " (still counts toward the max above)" : ""}:
          </p>
          <div className="flex flex-wrap gap-1.5">
            {legacy.map((v) => (
              <button
                key={v}
                type="button"
                className="rounded border border-amber-500/50 bg-background px-1.5 py-0.5 hover:bg-amber-500/20"
                onClick={() => onToggle(v)}
                title="Remove"
              >
                {v} ×
              </button>
            ))}
          </div>
        </div>
      ) : null}
    </div>
  );
}

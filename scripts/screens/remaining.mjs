#!/usr/bin/env node
/**
 * The last sixteen scaffolds, across six flows.
 *
 * Grouped into one generator because they share composers rather than because
 * they share a flow — which is the same argument the rest of this work has
 * been making. The four singletons (saved_favorites, qr_share, verifications,
 * search) are each ONE screen carrying four to seven declared states, so they
 * are the densest state work left in the app and the least screen-shaped.
 *
 *   notifications_center      loading / empty / populated / search_filtered /
 *                             category_filtered / muted_banner — six states of
 *                             one list.
 *   saved_favorites_hub       artists / bands / studios / marketplace / empty /
 *                             grid_view / list_view — a tabbed hub whose view
 *                             mode is also a state.
 *   verification_screen       a four-step wizard as four states, not four
 *                             screens.
 *   qr_share_flow             home / options / preview.
 *   universal_search          home / live_typing / results / empty.
 *
 * No new CSS.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

assertIcons(["arrow-forward", "search", "add", "check", "cancel", "bell", "send", "heart"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0">${title}</h1>
    </header>`;

const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

const rowSwitch = (label, meta, on) => `<div class="ml-check-row">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        <button class="ml-switch" type="button" role="switch" aria-checked="${on}" aria-label="${label}"></button>
      </div>`;

/* ══ FLOW 13 — Notifications ══════════════════════════════════════════════ */
const N = "/preview/flows/notifications/";
/* ══ the four singletons ══════════════════════════════════════════════════ */

finish("remaining: ");

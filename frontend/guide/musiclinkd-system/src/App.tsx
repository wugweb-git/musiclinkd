import React, { useEffect, useMemo, useState } from "react";
import {
  Accordion,
  ActivationCard,
  Avatar,
  AvatarGroup,
  AvatarGroupCluster,
  Badge,
  BannerCallout,
  BannerOverlay,
  BannerSlim,
  BannerUpsell,
  Box,
  Button,
  ButtonGroup,
  ButtonLink,
  ButtonSocial,
  ButtonToggle,
  Checkbox,
  ColorSchemeProvider,
  ComboBox,
  Datapoint,
  DefaultLabelProvider,
  Divider,
  Dropdown,
  Fieldset,
  Flex,
  HelpButton,
  Heading,
  Icon,
  IconButton,
  IconButtonFloating,
  IconButtonLink,
  IconCompact,
  Image,
  Indicator,
  Label,
  Letterbox,
  Link,
  List,
  Mask,
  Masonry,
  Modal,
  ModalAlert,
  NumberField,
  OverlayPanel,
  PageHeader,
  Pog,
  Popover,
  PopoverMessage,
  Pulsar,
  RadioGroup,
  SearchField,
  SegmentedControl,
  SelectList,
  SheetMobile,
  SideNavigation,
  Spinner,
  Status,
  Sticky,
  Switch,
  Table,
  Tabs,
  Tag,
  TapArea,
  TapAreaLink,
  Text,
  TextArea,
  TextCompact,
  TextField,
  TextUI,
  Toast,
  Tooltip,
} from "gestalt/dist/gestalt.es.js";

import iconNames from "./generated/icons.json";
import { WIDGETS, WIDGET_CATEGORIES_ORDER, type WidgetEntry } from "./widgets-mirror";
import {
  parseTokenCss,
  splitColorGroup,
  type TokenGroup,
  BREAKPOINTS,
  BREAKPOINTS_SOURCE,
  GRID,
  GRID_SOURCE,
  Z_INDEX,
  Z_INDEX_SOURCE,
  Z_INDEX_GAP,
  BORDER,
  BORDER_SOURCE,
  BORDER_GAP,
  MOTION_NAMES,
  MOTION_SOURCE,
  MOTION_GAP,
} from "./tokens";
// Inlined at build time (esbuild text loader) instead of fetched at runtime,
// so this app works as a plain double-clicked file:// page — no local
// server required.
import lightTokensCss from "../variables.css";
import darkTokensCss from "../variables-dark.css";
// Real brand mark SVGs, imported straight from the actual source in
// frontend/guide/brand/assets/ (esbuild text loader) — same two single-path,
// single-fill files used in frontend/guide/brandkit/index.html. Not copied
// or hand-transcribed; this is the literal file content.
import faviconMarkSvgRaw from "../../brand/assets/favicon-mark.svg";
import markGreySvgRaw from "../../brand/assets/mark-grey.svg";

// ---------------------------------------------------------------------------
// Every component below is the REAL import from the `gestalt` package (the
// same one installed in /admin) — not a mockup, not a screenshot. Interactive
// ones carry real React state so they actually respond to clicks and typing.
// Coverage: every exported Gestalt component except a short, deliberate
// exclusion list at the bottom of this file (infra-only pieces with no
// visual output, and Video which needs a real media file we don't have).
// ---------------------------------------------------------------------------

// A trivial, self-generated SVG placeholder — used anywhere Gestalt needs an
// image `src`. Not sourced from Pinterest's site; drawn here so there's no
// question about licensing.
function placeholderImg(hex: string, label: string, w = 320, h = 320) {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}"><rect width="100%" height="100%" fill="${hex}"/><text x="50%" y="50%" font-family="sans-serif" font-size="20" fill="white" text-anchor="middle" dy=".3em">${label}</text></svg>`;
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

type Category =
  | "Actions"
  | "Forms"
  | "Feedback"
  | "Data Display"
  | "Navigation"
  | "Overlays"
  | "Typography & Layout"
  | "Media & Gallery";

type Demo = {
  name: string;
  category: Category;
  description: string;
  render: () => React.ReactNode;
};

// ---- Actions ---------------------------------------------------------------

function ButtonDemo() {
  const [count, setCount] = useState(0);
  return (
    <Flex direction="column" gap={2}>
      <Button text={`Clicked ${count} times`} color="red" onClick={() => setCount((c) => c + 1)} />
      <ButtonGroup>
        <Button text="Secondary" color="gray" size="sm" />
        <ButtonLink text="Link" href="#" size="sm" />
      </ButtonGroup>
    </Flex>
  );
}

function ButtonToggleDemo() {
  const [selected, setSelected] = useState("week");
  return (
    <Flex gap={1}>
      {["day", "week", "month"].map((v) => (
        <ButtonToggle key={v} text={v} selected={selected === v} onClick={() => setSelected(v)} />
      ))}
    </Flex>
  );
}

function ButtonSocialDemo() {
  return <ButtonSocial type="continue" service="google" onClick={() => {}} />;
}

function PogDemo() {
  return (
    <Flex gap={2}>
      <Pog icon="heart" iconColor="red" />
      <Pog icon="share" bgColor="gray" />
    </Flex>
  );
}

function IconButtonFloatingDemo() {
  return (
    <IconButtonFloating
      accessibilityLabel="Add track"
      accessibilityPopupRole="menu"
      icon="add"
      onClick={() => {}}
      tooltip={{ text: "Add track" }}
    />
  );
}

// ---- Forms ------------------------------------------------------------------

function TextFieldDemo() {
  const [value, setValue] = useState("");
  return (
    <TextField id="demo-textfield" label="Artist name" placeholder="Type something…" value={value} onChange={({ value }) => setValue(value)} />
  );
}

function TextAreaDemo() {
  const [value, setValue] = useState("");
  return <TextArea id="demo-textarea" label="Bio" placeholder="Tell us about yourself" value={value} onChange={({ value }) => setValue(value)} />;
}

function NumberFieldDemo() {
  const [value, setValue] = useState<number | undefined>(120);
  return <NumberField id="demo-number" label="BPM" value={value} onChange={({ value }) => setValue(value)} />;
}

function SearchFieldDemo() {
  const [value, setValue] = useState("");
  return <SearchField id="demo-search" accessibilityLabel="Search tracks" placeholder="Search tracks…" value={value} onChange={({ value }) => setValue(value)} />;
}

function SelectListDemo() {
  const [value, setValue] = useState("pop");
  return (
    <SelectList id="demo-select" name="genre" label="Genre" value={value} onChange={({ value }) => setValue(value)}>
      <SelectList.Option label="Pop" value="pop" />
      <SelectList.Option label="Hip-Hop" value="hiphop" />
      <SelectList.Option label="Electronic" value="electronic" />
      <SelectList.Option label="Indie" value="indie" />
    </SelectList>
  );
}

function ComboBoxDemo() {
  const [inputValue, setInputValue] = useState("");
  const options = ["Vocalist", "Producer", "Session Drummer", "Mix Engineer", "Songwriter"]
    .filter((o) => o.toLowerCase().includes(inputValue.toLowerCase()))
    .map((o) => ({ label: o, value: o }));
  return (
    <ComboBox
      id="demo-combobox"
      label="Role"
      inputValue={inputValue}
      noResultText="No matches"
      options={options}
      onChange={({ value }) => setInputValue(value)}
      onSelect={({ item }) => setInputValue(item.value)}
    />
  );
}

function RadioGroupDemo() {
  const [value, setValue] = useState("indie");
  return (
    <RadioGroup id="demo-radio" legend="Plan" direction="row">
      {[
        { id: "indie", label: "Indie" },
        { id: "pro", label: "Pro" },
      ].map((opt) => (
        <RadioGroup.RadioButton
          key={opt.id}
          id={opt.id}
          label={opt.label}
          name="plan"
          value={opt.id}
          checked={value === opt.id}
          onChange={() => setValue(opt.id)}
        />
      ))}
    </RadioGroup>
  );
}

function CheckboxDemo() {
  const [checked, setChecked] = useState(true);
  return <Checkbox id="demo-checkbox" label={checked ? "Subscribed" : "Not subscribed"} checked={checked} onChange={({ checked }) => setChecked(checked)} />;
}

function SwitchDemo() {
  const [on, setOn] = useState(false);
  return (
    <Flex alignItems="center" gap={3}>
      <Switch id="demo-switch" switched={on} onChange={({ value }) => setOn(value)} />
      <Text>{on ? "Enabled" : "Disabled"}</Text>
    </Flex>
  );
}

function FieldsetDemo() {
  const [a, setA] = useState(true);
  const [b, setB] = useState(false);
  return (
    <Fieldset legend="Notify me about">
      <Flex direction="column" gap={2}>
        <Checkbox id="fs-a" label="New releases" checked={a} onChange={({ checked }) => setA(checked)} />
        <Checkbox id="fs-b" label="Comments" checked={b} onChange={({ checked }) => setB(checked)} />
      </Flex>
    </Fieldset>
  );
}

function LabelDemo() {
  return (
    <Flex direction="column" gap={1}>
      <Label htmlFor="label-demo-input">Display name</Label>
      <input id="label-demo-input" style={{ padding: 8, borderRadius: 8, border: "1px solid #ccc" }} placeholder="Type here" />
    </Flex>
  );
}

// ---- Feedback ---------------------------------------------------------------

function ToastDemo() {
  return <Toast text="Track uploaded" type="success" thumbnail={{ icon: <Icon icon="check-circle" accessibilityLabel="" /> }} />;
}

function ActivationCardDemo() {
  return (
    <ActivationCard
      status="pending"
      statusMessage="In progress"
      title="Verify your artist profile"
      message="Confirm your identity to unlock payouts."
      link={{ href: "#", label: "Get verified", accessibilityLabel: "Get verified" }}
    />
  );
}

function BannerUpsellDemo() {
  return (
    <BannerUpsell
      title="Go Pro"
      message="Unlock advanced analytics and unlimited uploads."
      primaryAction={{ label: "Upgrade", accessibilityLabel: "Upgrade to Pro", role: "button", onClick: () => {} }}
    />
  );
}

// BannerOverlay renders position: fixed to the viewport (confirmed in its own
// source, BannerOverlay.tsx) — so like Modal/Popover/Toast elsewhere in this
// file, it's shown behind a trigger button rather than mounted open by
// default, or it would cover the whole app on load.
function BannerOverlayDemo() {
  const [show, setShow] = useState(false);
  return show ? (
    <BannerOverlay
      message="Discover trending remixes in the app!"
      offset={{ top: 130, bottom: 24 }}
      onDismiss={() => setShow(false)}
      primaryAction={{ role: "button", onClick: () => setShow(false), label: "Get the app", accessibilityLabel: "Get the app" }}
    />
  ) : (
    <Button text="Show BannerOverlay" color="gray" onClick={() => setShow(true)} />
  );
}

// ---- Data Display -------------------------------------------------------------

function TagDemo() {
  const [tags, setTags] = useState(["Indie", "Synth-pop", "Remix"]);
  return (
    <Flex gap={1} wrap>
      {tags.map((t) => (
        <Tag key={t} text={t} onRemove={() => setTags((cur) => cur.filter((x) => x !== t))} />
      ))}
    </Flex>
  );
}

function ListDemo() {
  return (
    <List label="Track credits" type="bare">
      <List.Item text="Written by Ava Chen" />
      <List.Item text="Produced by Ravi Nair" />
      <List.Item text="Mixed by Mei Tanaka" />
    </List>
  );
}

function TableDemo() {
  const rows = [
    { track: "Neon Tide", plays: "482K", status: "Live" },
    { track: "Glass Room", plays: "112K", status: "Live" },
    { track: "Afterglow (demo)", plays: "—", status: "Draft" },
  ];
  return (
    <Table accessibilityLabel="Recent uploads">
      <Table.Header>
        <Table.Row>
          <Table.HeaderCell><Text weight="bold">Track</Text></Table.HeaderCell>
          <Table.HeaderCell><Text weight="bold">Plays</Text></Table.HeaderCell>
          <Table.HeaderCell><Text weight="bold">Status</Text></Table.HeaderCell>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {rows.map((r) => (
          <Table.Row key={r.track}>
            <Table.Cell><Text>{r.track}</Text></Table.Cell>
            <Table.Cell><Text>{r.plays}</Text></Table.Cell>
            <Table.Cell><Status type={r.status === "Live" ? "ok" : "unstarted"} title={r.status} /></Table.Cell>
          </Table.Row>
        ))}
      </Table.Body>
    </Table>
  );
}

// ---- Overlays -----------------------------------------------------------------

function ModalDemo() {
  const [open, setOpen] = useState(false);
  return (
    <>
      <Button text="Open modal" onClick={() => setOpen(true)} />
      {open && (
        <Modal
          accessibilityModalLabel="Demo modal"
          heading="Release a new track"
          onDismiss={() => setOpen(false)}
          footer={
            <Flex justifyContent="end" gap={2}>
              <Button text="Cancel" color="gray" onClick={() => setOpen(false)} />
              <Button text="Publish" color="red" onClick={() => setOpen(false)} />
            </Flex>
          }
        >
          <Box padding={2}>
            <Text>This is a real, live Gestalt Modal — not a screenshot.</Text>
          </Box>
        </Modal>
      )}
    </>
  );
}

function ModalAlertDemo() {
  const [open, setOpen] = useState(false);
  return (
    <>
      <Button text="Delete track" color="red" onClick={() => setOpen(true)} />
      {open && (
        <ModalAlert
          accessibilityModalLabel="Confirm delete"
          heading="Delete this track?"
          type="error"
          onDismiss={() => setOpen(false)}
          primaryAction={{ accessibilityLabel: "Delete", label: "Delete", onClick: () => setOpen(false), color: "red" }}
          secondaryAction={{ accessibilityLabel: "Cancel", label: "Cancel", onClick: () => setOpen(false) }}
        >
          <Text>This can't be undone.</Text>
        </ModalAlert>
      )}
    </>
  );
}

function PopoverDemo() {
  const [open, setOpen] = useState(false);
  const anchorRef = React.useRef<HTMLButtonElement>(null);
  return (
    <Box>
      <Button ref={anchorRef} text="Show popover" onClick={() => setOpen((o) => !o)} />
      {open && anchorRef.current && (
        <Popover anchor={anchorRef.current} onDismiss={() => setOpen(false)} idealDirection="down">
          <Box padding={3} width={220}>
            <Text weight="bold">Real Popover</Text>
            <Text size="200">Positioned live against its anchor button.</Text>
          </Box>
        </Popover>
      )}
    </Box>
  );
}

function PopoverMessageDemo() {
  const [open, setOpen] = useState(false);
  const anchorRef = React.useRef<HTMLButtonElement>(null);
  return (
    <Box>
      <Button ref={anchorRef} text="Show tip" onClick={() => setOpen((o) => !o)} />
      {open && anchorRef.current && (
        <PopoverMessage anchor={anchorRef.current} onDismiss={() => setOpen(false)} message="Drag to reorder your tracklist." type="education" />
      )}
    </Box>
  );
}

function DropdownDemo() {
  const [open, setOpen] = useState(false);
  const [selected, setSelected] = useState<{ label: string; value: string } | null>(null);
  const anchorRef = React.useRef<HTMLButtonElement>(null);
  const options = [
    { label: "Newest first", value: "newest" },
    { label: "Most played", value: "plays" },
    { label: "Alphabetical", value: "az" },
  ];
  return (
    <Box>
      <Button ref={anchorRef} text={selected ? selected.label : "Sort by"} onClick={() => setOpen((o) => !o)} />
      {open && anchorRef.current && (
        <Dropdown anchor={anchorRef.current} id="sort-dropdown" onDismiss={() => setOpen(false)}>
          {options.map((opt) => (
            <Dropdown.Item key={opt.value} option={opt} selected={selected} onSelect={({ item }) => { setSelected(item); setOpen(false); }} />
          ))}
        </Dropdown>
      )}
    </Box>
  );
}

function OverlayPanelDemo() {
  const [open, setOpen] = useState(false);
  return (
    <>
      <Button text="Open panel" onClick={() => setOpen(true)} />
      {open && (
        <OverlayPanel accessibilityLabel="Track details" heading="Track details" onDismiss={() => setOpen(false)}>
          <Box padding={2}>
            <Text>Real OverlayPanel, slides in from the side.</Text>
          </Box>
        </OverlayPanel>
      )}
    </>
  );
}

function SheetMobileDemo() {
  const [open, setOpen] = useState(false);
  return (
    <>
      <Button text="Open sheet" onClick={() => setOpen(true)} />
      {open && (
        <SheetMobile heading="Share track" onDismiss={() => setOpen(false)}>
          <Box padding={2}>
            <Text>Real SheetMobile, bottom-anchored.</Text>
          </Box>
        </SheetMobile>
      )}
    </>
  );
}

// ---- Typography & Layout -------------------------------------------------------

function AccordionDemo() {
  // Real Accordion.Expandable — controlled expandedIndex, actually
  // collapses/expands on click (the base Accordion has no such behavior —
  // it's a static container, which was a bug fixed here).
  const [expandedIndex, setExpandedIndex] = useState<number | null | undefined>(0);
  return (
    <Accordion.Expandable
      id="demo-accordion"
      expandedIndex={expandedIndex}
      onExpandedChange={setExpandedIndex}
      items={[
        { title: "Royalty split", icon: "invoice", children: <Text>60% artist, 30% producer, 10% label — configurable per release.</Text> },
        { title: "Payout schedule", icon: "calendar", children: <Text>Paid out monthly, on the 1st, once you clear the $25 minimum.</Text> },
      ]}
    />
  );
}

function TabsDemo() {
  const [active, setActive] = useState(0);
  return (
    <Tabs
      activeTabIndex={active}
      onChange={({ activeTabIndex }) => setActive(activeTabIndex)}
      tabs={[
        { href: "#", text: "Tracks" },
        { href: "#", text: "Albums" },
        { href: "#", text: "Credits" },
      ]}
    />
  );
}

function SegmentedControlDemo() {
  const [index, setIndex] = useState(0);
  return <SegmentedControl items={["Grid", "List"]} selectedItemIndex={index} onChange={({ activeIndex }) => setIndex(activeIndex)} />;
}

function HelpButtonDemo() {
  return <HelpButton accessibilityLabel="What is a royalty split?" accessibilityPopoverLabel="Royalty split help" text="A royalty split defines how earnings are divided among collaborators." />;
}

function PulsarDemo() {
  return (
    <Box position="relative" width={40} height={40} display="flex" alignItems="center" justifyContent="center">
      <Pulsar paused={false} />
      <Box position="absolute"><Icon icon="pin" accessibilityLabel="New" /></Box>
    </Box>
  );
}

// ---- Navigation -------------------------------------------------------------

// Real usage pattern taken directly from Gestalt's own docs/examples/sidenavigation
// (activeItemExample.tsx) — SideNavigation.Section/.TopItem are real static
// subcomponents (SideNavigation.tsx assigns them: SideNavigation.Section,
// .TopItem, .NestedItem, .Group, .NestedGroup), not fabricated.
function SideNavigationDemo() {
  return (
    <Box maxHeight={220} overflow="scroll" maxWidth={220} borderStyle="sm" rounding={2} padding={2}>
      <SideNavigation accessibilityLabel="MusicLinkd navigation example">
        <SideNavigation.Section label="Library">
          <SideNavigation.TopItem href="#" label="Releases" active="page" />
          <SideNavigation.TopItem href="#" label="Playlists" />
        </SideNavigation.Section>
        <SideNavigation.Section label="Account">
          <SideNavigation.TopItem href="#" label="Payouts" badge={{ text: "New", type: "info" }} />
          <SideNavigation.TopItem href="#" label="Settings" />
        </SideNavigation.Section>
      </SideNavigation>
    </Box>
  );
}

// Adapted from Gestalt's own docs/examples/sticky/main.tsx — a scrollable
// Box with two Sticky children that pin to the top as you scroll past them.
function StickyDemo() {
  return (
    <Box color="default" height={140} maxWidth={220} overflow="scroll" tabIndex={0} borderStyle="sm" rounding={2}>
      <Box height={360}>
        <Sticky top={0}>
          <Box alignItems="center" color="infoWeak" display="flex" height={32} padding={2}>
            <Text size="100">Sticks on scroll</Text>
          </Box>
        </Sticky>
        <Box padding={2}>
          <Text size="100">Scroll down…</Text>
          <Text size="100">Keep scrolling…</Text>
          <Text size="100">Almost there…</Text>
          <Text size="100">Still scrolling…</Text>
        </Box>
      </Box>
    </Box>
  );
}

// ---- Media & Gallery -------------------------------------------------------------

function ImageDemo() {
  return (
    <Box width={160} height={160} rounding={2} overflow="hidden">
      <Image alt="Album artwork placeholder" src={placeholderImg("#7c3aed", "Cover")} color="#7c3aed" naturalWidth={320} naturalHeight={320} />
    </Box>
  );
}

function MaskDemo() {
  return (
    <Mask rounding="circle" width={96} height={96}>
      <Image alt="Artist avatar placeholder" src={placeholderImg("#ea580c", "Artist")} color="#ea580c" naturalWidth={320} naturalHeight={320} />
    </Mask>
  );
}

// Letterbox.width is a required prop (not maxWidth) — it needs a literal
// pixel value for aspect-ratio math, so it can't be made fluid. Shrunk from
// 220 to 160 so it fits inside the smallest comp-grid cell (280px minmax
// minus card padding) instead of overflowing it.
function LetterboxDemo() {
  return (
    <Box maxWidth={160}>
      <Letterbox contentAspectRatio={1} height={110} width={160}>
        <Image alt="Wide artwork placeholder" src={placeholderImg("#0891b2", "Banner", 640, 640)} color="#0891b2" naturalWidth={640} naturalHeight={640} />
      </Letterbox>
    </Box>
  );
}

const DEMOS: Demo[] = [
  { name: "Button", category: "Actions", description: "Real onClick handler, live click counter.", render: () => <ButtonDemo /> },
  { name: "IconButton + Tooltip", category: "Actions", description: "Hover/focus to see the real Tooltip.", render: () => (
      <Tooltip text="Play this track"><IconButton icon="play" accessibilityLabel="Play" onClick={() => {}} /></Tooltip>
    ) },
  { name: "IconButtonLink", category: "Actions", description: "Anchor-rendered icon button.", render: () => <IconButtonLink icon="download" accessibilityLabel="Download" href="#" /> },
  { name: "IconButtonFloating", category: "Actions", description: "Floating action button with tooltip.", render: () => <IconButtonFloatingDemo /> },
  { name: "ButtonToggle", category: "Actions", description: "Segmented toggle with live selection state.", render: () => <ButtonToggleDemo /> },
  { name: "ButtonSocial", category: "Actions", description: "Branded social auth button.", render: () => <ButtonSocialDemo /> },
  { name: "Pog", category: "Actions", description: "Circular icon surface, used inside other controls.", render: () => <PogDemo /> },
  { name: "TapArea", category: "Actions", description: "Custom tappable region.", render: () => (
      <TapArea onTap={() => {}} rounding={2}><Box padding={3} color="secondary" rounding={2}><Text align="center">Tap me</Text></Box></TapArea>
    ) },
  { name: "TapAreaLink", category: "Actions", description: "Tappable region that's really an anchor.", render: () => (
      <TapAreaLink href="#" rounding={2}><Box padding={3} color="secondary" rounding={2}><Text align="center">Tap this link</Text></Box></TapAreaLink>
    ) },

  { name: "TextField", category: "Forms", description: "Controlled input, live value.", render: () => <TextFieldDemo /> },
  { name: "TextArea", category: "Forms", description: "Multi-line controlled input.", render: () => <TextAreaDemo /> },
  { name: "NumberField", category: "Forms", description: "Numeric stepper input.", render: () => <NumberFieldDemo /> },
  { name: "SearchField", category: "Forms", description: "Search input with clear button.", render: () => <SearchFieldDemo /> },
  { name: "Checkbox", category: "Forms", description: "Toggles real checked state.", render: () => <CheckboxDemo /> },
  { name: "Switch", category: "Forms", description: "On/off toggle, live state.", render: () => <SwitchDemo /> },
  { name: "RadioGroup", category: "Forms", description: "Two live radio options.", render: () => <RadioGroupDemo /> },
  { name: "SelectList", category: "Forms", description: "Native select, live value.", render: () => <SelectListDemo /> },
  { name: "ComboBox", category: "Forms", description: "Typeahead filtering real options as you type.", render: () => <ComboBoxDemo /> },
  { name: "Fieldset", category: "Forms", description: "Groups related fields under one legend.", render: () => <FieldsetDemo /> },
  { name: "Label", category: "Forms", description: "Accessible label bound to a field.", render: () => <LabelDemo /> },

  { name: "Spinner", category: "Feedback", description: "Loading indicator.", render: () => <Spinner show accessibilityLabel="Loading" /> },
  { name: "BannerCallout", category: "Feedback", description: "Inline callout message.", render: () => <BannerCallout type="success" message="Track uploaded successfully." iconAccessibilityLabel="Success" /> },
  { name: "BannerSlim", category: "Feedback", description: "Compact status banner.", render: () => <BannerSlim type="warning" message="Your session expires in 5 minutes." iconAccessibilityLabel="Warning" /> },
  { name: "BannerUpsell", category: "Feedback", description: "Promotional banner with an action.", render: () => <BannerUpsellDemo /> },
  { name: "ActivationCard", category: "Feedback", description: "Onboarding step card with status.", render: () => <ActivationCardDemo /> },
  { name: "Toast", category: "Feedback", description: "Ephemeral confirmation message.", render: () => <ToastDemo /> },
  { name: "Status", category: "Feedback", description: "State indicator icon.", render: () => <Status type="ok" title="Live" /> },
  { name: "BannerOverlay", category: "Feedback", description: "Fixed-position promo overlay, dismissible.", render: () => <BannerOverlayDemo /> },
  { name: "Indicator", category: "Feedback", description: "Numeric badge indicator.", render: () => (
      <Box position="relative" width={32} height={32}><Icon icon="bell" accessibilityLabel="Notifications" /><Indicator accessibilityLabel="3 new" count={3} /></Box>
    ) },

  { name: "Avatar", category: "Data Display", description: "User avatar with initials fallback.", render: () => <Avatar name="Vedanshu K" size="md" /> },
  { name: "AvatarGroup", category: "Data Display", description: "Stacked collaborators.", render: () => <AvatarGroup accessibilityLabel="Collaborators" collaborators={[{ name: "Ava" }, { name: "Ravi" }, { name: "Mei" }]} size="md" /> },
  { name: "AvatarGroupCluster", category: "Data Display", description: "Compact clustered avatars.", render: () => <AvatarGroupCluster collaborators={[{ name: "Ava" }, { name: "Ravi" }, { name: "Mei" }]} size="md" /> },
  { name: "Badge", category: "Data Display", description: "Status label.", render: () => <Badge text="New release" type="success" /> },
  { name: "Tag", category: "Data Display", description: "Removable chips — click x to remove.", render: () => <TagDemo /> },
  { name: "Datapoint", category: "Data Display", description: "Metric with trend.", render: () => <Datapoint size="md" title="Monthly listeners" value="482K" trend={{ value: 12, accessibilityLabel: "Trending up" }} /> },
  { name: "List", category: "Data Display", description: "Structured bullet list.", render: () => <ListDemo /> },
  { name: "Table", category: "Data Display", description: "Real Table with header/body/rows.", render: () => <TableDemo /> },
  { name: "Text / TextUI / TextCompact", category: "Data Display", description: "Three text density variants.", render: () => (
      <Flex direction="column" gap={1}><Text>Text — default body copy</Text><TextUI size="md">TextUI — UI copy</TextUI><TextCompact>TextCompact — dense copy</TextCompact></Flex>
    ) },

  { name: "Modal", category: "Overlays", description: "Real Modal, opens on click.", render: () => <ModalDemo /> },
  { name: "ModalAlert", category: "Overlays", description: "Confirmation dialog for risky actions.", render: () => <ModalAlertDemo /> },
  { name: "Popover", category: "Overlays", description: "Live-positioned popover.", render: () => <PopoverDemo /> },
  { name: "PopoverMessage", category: "Overlays", description: "Educational tooltip-style popover.", render: () => <PopoverMessageDemo /> },
  { name: "Dropdown", category: "Overlays", description: "Real Dropdown menu with selectable items.", render: () => <DropdownDemo /> },
  { name: "OverlayPanel", category: "Overlays", description: "Side-anchored panel.", render: () => <OverlayPanelDemo /> },
  { name: "SheetMobile", category: "Overlays", description: "Bottom sheet overlay.", render: () => <SheetMobileDemo /> },

  { name: "Heading / Text", category: "Typography & Layout", description: "Typographic scale.", render: () => (
      <Flex direction="column" gap={2}><Heading size="400">Heading 400</Heading><Text size="200">Body text at size 200.</Text></Flex>
    ) },
  { name: "Divider", category: "Typography & Layout", description: "Section divider.", render: () => <Box width={200}><Divider /></Box> },
  { name: "PageHeader", category: "Typography & Layout", description: "Page title block with actions.", render: () => <PageHeader title="Discover" subtext="New releases this week" /> },
  { name: "Accordion", category: "Typography & Layout", description: "Collapsible section, click to expand.", render: () => <AccordionDemo /> },
  { name: "Tabs", category: "Typography & Layout", description: "Real tab switching.", render: () => <TabsDemo /> },
  { name: "SegmentedControl", category: "Typography & Layout", description: "Two-way view switcher.", render: () => <SegmentedControlDemo /> },
  { name: "HelpButton", category: "Typography & Layout", description: "Inline contextual help.", render: () => <HelpButtonDemo /> },
  { name: "Link", category: "Typography & Layout", description: "Inline navigational link.", render: () => <Link href="#">View full discography</Link> },
  { name: "Pulsar", category: "Typography & Layout", description: "Ambient attention marker.", render: () => <PulsarDemo /> },
  { name: "IconCompact", category: "Typography & Layout", description: "Dense inline icon variant — its own smaller icon set, separate from Icon.", render: () => (
      <Flex gap={2}><IconCompact accessibilityLabel="Verified" icon="compact-check" /><IconCompact accessibilityLabel="Locked" icon="compact-lock" /></Flex>
    ) },

  { name: "SideNavigation", category: "Navigation", description: "Sectioned nav with real subcomponents (Section, TopItem, badges).", render: () => <SideNavigationDemo /> },
  { name: "Sticky", category: "Navigation", description: "Pins children to a scroll threshold — scroll the box to see it stick.", render: () => <StickyDemo /> },

  { name: "Image", category: "Media & Gallery", description: "Image with placeholder color.", render: () => <ImageDemo /> },
  { name: "Mask", category: "Media & Gallery", description: "Rounds/crops any child content.", render: () => <MaskDemo /> },
  { name: "Letterbox", category: "Media & Gallery", description: "Fits content to a fixed aspect ratio.", render: () => <LetterboxDemo /> },
];

const CATEGORIES: Category[] = ["Actions", "Forms", "Feedback", "Data Display", "Navigation", "Overlays", "Typography & Layout", "Media & Gallery"];

// Cross-checked against the real export list in gestalt/packages/gestalt/src/index.ts
// (every named export, not assumed from memory or from the old styleguide) —
// this was corrected on 2026-08-16 after an audit found 4 real exports
// (BannerOverlay, Layer, SideNavigation, Sticky) that were silently missing
// from both the demo set AND this note. BannerOverlay, SideNavigation, and
// Sticky are now demoed for real; only Layer remains undemoed, for the
// reason stated below.
const EXCLUDED_NOTE =
  "Not demoed here, on purpose: Video (needs a real media file), Collage/MasonryV2 (Masonry — the real component, not a stand-in — is demoed in the Blocks tab's Cover art gallery instead), SearchGuide/SearchGuideLink (search-index-specific), ScrollBoundaryContainer/ScrollFetch/TableOfContents (no standalone visual output), WashAnimated/TileData/TagData (Masonry-overlay-specific), Layer (a React createPortal wrapper — renders children outside the normal DOM tree with no visual output of its own; Modal/Popover/Toast use it internally rather than exposing it directly), Container (a Box wrapper that centers content and caps it at 800px wide — real layout behavior, but nothing to show as an isolated card). Providers and hooks (ColorSchemeProvider, useColorScheme, etc.) power this app itself rather than being shown as cards.";

function ComponentGallery({ query }: { query: string }) {
  const filtered = DEMOS.filter((d) => d.name.toLowerCase().includes(query.toLowerCase()) || d.category.toLowerCase().includes(query.toLowerCase()));
  return (
    <Box>
      {CATEGORIES.map((cat) => {
        const items = filtered.filter((d) => d.category === cat);
        if (items.length === 0) return null;
        return (
          <Box key={cat} marginBottom={8}>
            <Box marginBottom={3}><Heading size="300">{cat}</Heading></Box>
            <div className="ml-comp-grid">
              {items.map((d) => (
                <Box key={d.name} borderStyle="sm" rounding={3} padding={4} color="default">
                  <Box marginBottom={3}>
                    <Text weight="bold">{d.name}</Text>
                    <Text size="100" color="subtle">{d.description}</Text>
                  </Box>
                  <Box padding={2}>{d.render()}</Box>
                </Box>
              ))}
            </div>
          </Box>
        );
      })}
      {filtered.length === 0 && <Text color="subtle">No components match “{query}”.</Text>}
      <Box marginTop={6} padding={4} borderStyle="sm" rounding={3}>
        <Text size="100" color="subtle">{EXCLUDED_NOTE}</Text>
      </Box>
    </Box>
  );
}

// ---------------------------------------------------------------------------
// Blocks — real composed patterns, built from the components above, styled
// for a music platform. This is the "how these fit together" layer.
// ---------------------------------------------------------------------------

function ArtistProfileBlock() {
  const [following, setFollowing] = useState(false);
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={360}>
      <Flex gap={3} alignItems="center">
        <Avatar name="Mei Tanaka" size="lg" />
        <Box flex="grow">
          <Text weight="bold">Mei Tanaka</Text>
          <Text size="100" color="subtle">Producer · Tokyo</Text>
        </Box>
      </Flex>
      <Box marginTop={3} marginBottom={3}>
        <Flex gap={1} wrap>
          <Tag text="Electronic" onRemove={() => {}} />
          <Tag text="Ambient" onRemove={() => {}} />
        </Flex>
      </Box>
      <Button
        text={following ? "Following" : "Follow"}
        color={following ? "gray" : "red"}
        fullWidth
        onClick={() => setFollowing((f) => !f)}
      />
    </Box>
  );
}

function TrackRowBlock() {
  const [playing, setPlaying] = useState(false);
  return (
    <Box borderStyle="sm" rounding={3} padding={3} maxWidth={420}>
      <Flex gap={3} alignItems="center">
        <IconButton icon={playing ? "pause" : "play"} accessibilityLabel={playing ? "Pause" : "Play"} onClick={() => setPlaying((p) => !p)} bgColor="red" iconColor="white" />
        <Box flex="grow">
          <Text weight="bold">Neon Tide</Text>
          <Text size="100" color="subtle">Mei Tanaka · 3:42</Text>
        </Box>
        <Status type={playing ? "inProgress" : "ok"} title={playing ? "Playing" : "482K plays"} />
      </Flex>
    </Box>
  );
}

function UploadFormBlock() {
  const [title, setTitle] = useState("");
  const [genre, setGenre] = useState("pop");
  const [explicit, setExplicit] = useState(false);
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={360}>
      <Box marginBottom={3}><Heading size="300">Upload a release</Heading></Box>
      <Flex direction="column" gap={3}>
        <TextField id="block-upload-title" label="Track title" value={title} onChange={({ value }) => setTitle(value)} />
        <SelectList id="block-upload-genre" name="genre" label="Genre" value={genre} onChange={({ value }) => setGenre(value)}>
          <SelectList.Option label="Pop" value="pop" />
          <SelectList.Option label="Hip-Hop" value="hiphop" />
          <SelectList.Option label="Electronic" value="electronic" />
        </SelectList>
        <Checkbox id="block-upload-explicit" label="Contains explicit content" checked={explicit} onChange={({ checked }) => setExplicit(checked)} />
        <Button text={title ? `Upload "${title}"` : "Upload"} color="red" disabled={!title} onClick={() => {}} />
      </Flex>
    </Box>
  );
}

function StatsRowBlock() {
  return (
    <Flex gap={4} wrap>
      <Datapoint size="md" title="Monthly listeners" value="482K" trend={{ value: 12, accessibilityLabel: "Trending up" }} />
      <Datapoint size="md" title="Followers" value="18.2K" trend={{ value: 4, accessibilityLabel: "Trending up" }} />
      <Datapoint size="md" title="Saves" value="9,120" trend={{ value: -2, accessibilityLabel: "Trending down" }} />
    </Flex>
  );
}

function EmptyStateBlock() {
  return (
    <Box borderStyle="sm" rounding={4} padding={6} maxWidth={360} display="flex" direction="column" alignItems="center">
      <Icon icon="music-on" accessibilityLabel="" size={32} color="subtle" />
      <Box marginTop={3} marginBottom={1}><Heading size="300" align="center">No tracks yet</Heading></Box>
      <Box marginBottom={3}><Text align="center" color="subtle">Upload your first release to start building your catalog.</Text></Box>
      <Button text="Upload a track" color="red" onClick={() => {}} />
    </Box>
  );
}

function NotificationStackBlock() {
  return (
    <Flex direction="column" gap={2}>
      <BannerSlim type="success" message={'"Neon Tide" passed content review.'} iconAccessibilityLabel="Success" />
      <BannerSlim type="info" message="New payout method available." iconAccessibilityLabel="Info" />
      <BannerSlim type="warning" message="Update your tax info before month end." iconAccessibilityLabel="Warning" />
    </Flex>
  );
}

function NavHeaderBlock() {
  const [value, setValue] = useState("");
  return (
    <Box borderStyle="sm" rounding={3} padding={3}>
      <Flex gap={3} alignItems="center">
        <Text weight="bold">MusicLinkd</Text>
        <Box flex="grow" maxWidth={260}>
          <SearchField id="block-nav-search" accessibilityLabel="Search" placeholder="Search artists, tracks…" value={value} onChange={({ value }) => setValue(value)} />
        </Box>
        <IconButton icon="bell" accessibilityLabel="Notifications" />
        <Avatar name="Vedanshu K" size="sm" />
      </Flex>
    </Box>
  );
}

function GalleryBlock() {
  // The real Masonry component (Pinterest's own signature layout algorithm)
  // — not a fake Flex-wrap grid standing in for it. Column heights vary on
  // purpose to show the algorithm actually packing them, not just a uniform
  // CSS grid.
  const items = [
    { color: "#7c3aed", label: "A", h: 140 },
    { color: "#ea580c", label: "B", h: 90 },
    { color: "#0891b2", label: "C", h: 170 },
    { color: "#16a34a", label: "D", h: 110 },
    { color: "#db2777", label: "E", h: 130 },
    { color: "#ca8a04", label: "F", h: 95 },
  ];
  return (
    <Box width="100%" height={260} overflow="auto">
      <Masonry
        items={items}
        columnWidth={90}
        gutterWidth={8}
        layout="basic"
        renderItem={({ data }) => (
          <Box width={90} height={data.h} rounding={3} overflow="hidden">
            <Image alt={`Cover ${data.label}`} src={placeholderImg(data.color, data.label, 200, 200)} color={data.color} naturalWidth={200} naturalHeight={data.h} />
          </Box>
        )}
      />
    </Box>
  );
}

// Mobile bottom navigation bar — a real composed pattern (TapArea + Icon +
// Text, all real Gestalt primitives; Gestalt itself has no dedicated
// "BottomNav" component, same as it has no dedicated top nav bar — this is
// how a music app's tab bar is actually built from Gestalt's building
// blocks). Icons are real, existing names (home/home-fill, compass/
// compass-fill, add-circle, speech-ellipsis/speech-ellipsis-fill, person/
// person-fill) verified against the installed icon set. The active-state
// indicator dot uses this app's own --ml-accent (MusicLinkd's real brand
// pink) rather than Icon's `color="brandPrimary"`, which would resolve to
// Gestalt's own red — same reasoning as the rest of this file's red/pink
// split: Gestalt's real component options stay accurate to Gestalt, and
// MusicLinkd's own accent lives in this app's own chrome only.
function BottomNavBlock() {
  const items = [
    { key: "home", label: "Home", icon: "home", iconActive: "home-fill" },
    { key: "discover", label: "Discover", icon: "compass", iconActive: "compass-fill" },
    { key: "upload", label: "Upload", icon: "add-circle", iconActive: "add-circle" },
    { key: "inbox", label: "Inbox", icon: "speech-ellipsis", iconActive: "speech-ellipsis-fill" },
    { key: "profile", label: "Profile", icon: "person", iconActive: "person-fill" },
  ] as const;
  const [active, setActive] = useState<string>("home");
  return (
    <Box maxWidth={360} borderStyle="sm" rounding={4} overflow="hidden">
      <Box padding={4}>
        <Text size="100" color="subtle">Screen content sits above the bar — tap an item below.</Text>
      </Box>
      <Box borderStyle="sm" padding={1} display="flex" justifyContent="around">
        {items.map((item) => {
          const isActive = active === item.key;
          return (
            <TapArea key={item.key} onTap={() => setActive(item.key)} accessibilityLabel={item.label} rounding={2}>
              <Flex direction="column" alignItems="center" gap={1}>
                <Box height={3} width={3} rounding="circle" marginBottom={1} dangerouslySetInlineStyle={{ __style: { background: isActive ? "var(--ml-accent)" : "transparent" } }} />
                <Icon icon={isActive ? item.iconActive : item.icon} accessibilityLabel="" color={isActive ? "dark" : "subtle"} size={20} />
                <Text size="100" color={isActive ? "default" : "subtle"}>{item.label}</Text>
              </Flex>
            </TapArea>
          );
        })}
      </Box>
    </Box>
  );
}

// Trust tiers — sourced directly from the real admin/components/shared/
// trust-badge.tsx (TIER_STYLE), not invented: "Verified Artist", "Top Host",
// "Rising Talent", "Studio Partner". That file uses lucide-react icons
// (ShieldCheck, Award, TrendingUp, Building2); mapped here to the closest
// real Gestalt icon names (protect, ribbon, trending, building-business —
// all confirmed present in the installed icon set), since lucide isn't part
// of this Gestalt-only app.
function TrustBadgesBlock() {
  const tiers = [
    { label: "Verified Artist", icon: "protect" as const, type: "info" as const },
    { label: "Top Host", icon: "ribbon" as const, type: "success" as const },
    { label: "Rising Talent", icon: "trending" as const, type: "recommendation" as const },
    { label: "Studio Partner", icon: "building-business" as const, type: "neutral" as const },
  ];
  return (
    <Flex gap={2} wrap>
      {tiers.map((t) => (
        <Box key={t.label} borderStyle="sm" rounding="pill" paddingX={3} paddingY={2} display="flex" alignItems="center">
          <Flex gap={1} alignItems="center">
            <Icon icon={t.icon} accessibilityLabel="" size={14} />
            <Text size="100" weight="bold">{t.label}</Text>
          </Flex>
        </Box>
      ))}
    </Flex>
  );
}

function HeroBlock() {
  return (
    <Box
      rounding={4}
      padding={6}
      overflow="hidden"
      dangerouslySetInlineStyle={{ __style: { backgroundImage: "linear-gradient(135deg, #EA4C89 0%, #0D0B26 100%)" } }}
    >
      <Text color="light" size="100" weight="bold">FEATURED THIS WEEK</Text>
      <Box marginTop={2} marginBottom={2}><Heading color="light" size="400">Studios booking Dolby Atmos rooms</Heading></Box>
      <Text color="light">12 verified rooms near you, ready this weekend.</Text>
      <Box marginTop={4}><Button text="Browse studios" color="white" /></Box>
    </Box>
  );
}

function MetricsScorecardBlock() {
  const metrics = [
    { icon: "graph-bar" as const, title: "Monthly listeners", value: "482K", trend: 12 },
    { icon: "people" as const, title: "Followers", value: "18.2K", trend: 4 },
    { icon: "gauge" as const, title: "Engagement rate", value: "6.4%", trend: -2 },
    { icon: "rocketship" as const, title: "Profile views", value: "9,120", trend: 18 },
  ];
  return (
    <div className="ml-comp-grid">
      {metrics.map((m) => (
        <Box key={m.title} borderStyle="sm" rounding={3} padding={3}>
          <Flex gap={2} alignItems="center">
            <Icon icon={m.icon} accessibilityLabel="" color="subtle" />
            <Text size="100" color="subtle">{m.title}</Text>
          </Flex>
          <Box marginTop={2}>
            <Datapoint size="md" title="" value={m.value} trend={{ value: m.trend, accessibilityLabel: m.trend >= 0 ? "Trending up" : "Trending down" }} />
          </Box>
        </Box>
      ))}
    </div>
  );
}

function ActivityFeedBlock() {
  const items = [
    { name: "Ravi Nair", action: "commented on your track", detail: '"Neon Tide"', time: "2m" },
    { name: "Mei Tanaka", action: "booked", detail: "Bloom Studio A", time: "1h" },
    { name: "Ava Chen", action: "followed you", detail: "", time: "3h" },
  ];
  return (
    <Flex direction="column" gap={3}>
      {items.map((it, i) => (
        <Flex key={i} gap={3} alignItems="start">
          <Avatar name={it.name} size="sm" />
          <Box flex="grow">
            <Text size="100"><Text inline weight="bold" size="100">{it.name}</Text> {it.action} {it.detail}</Text>
            <Text size="100" color="subtle">{it.time} ago</Text>
          </Box>
        </Flex>
      ))}
    </Flex>
  );
}

function SearchAndFiltersBlock() {
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const anchorRef = React.useRef<HTMLButtonElement>(null);
  const [pills, setPills] = useState<Record<string, boolean>>({ "Dolby Atmos": true, "Available today": false, "Under ₹2,000/hr": false });
  return (
    <Box>
      <Flex gap={2}>
        <Box flex="grow"><SearchField id="block-search-filters" accessibilityLabel="Search studios" placeholder="Search studios, artists, gear…" value={query} onChange={({ value }) => setQuery(value)} /></Box>
        <Button ref={anchorRef} text="Filters" color="gray" onClick={() => setOpen((o) => !o)} />
      </Flex>
      {open && anchorRef.current && (
        <Dropdown anchor={anchorRef.current} id="block-filters-dropdown" onDismiss={() => setOpen(false)}>
          <Dropdown.Item option={{ label: "Distance: nearest first", value: "distance" }} onSelect={() => setOpen(false)} />
          <Dropdown.Item option={{ label: "Price: low to high", value: "price" }} onSelect={() => setOpen(false)} />
        </Dropdown>
      )}
      <Box marginTop={3}>
        <Flex gap={1} wrap>
          {Object.entries(pills).map(([label, active]) => (
            <Tag key={label} text={label} onRemove={active ? () => setPills((p) => ({ ...p, [label]: false })) : undefined} />
          ))}
        </Flex>
      </Box>
    </Box>
  );
}

function QuickActionsBlock() {
  const actions = [
    { icon: "upload-feed" as const, label: "Upload" },
    { icon: "envelope" as const, label: "Message" },
    { icon: "calendar" as const, label: "Book" },
    { icon: "search" as const, label: "Discover" },
  ];
  return (
    <Flex gap={4}>
      {actions.map((a) => (
        <Flex key={a.label} direction="column" alignItems="center" gap={1}>
          <IconButton icon={a.icon} accessibilityLabel={a.label} bgColor="lightGray" size="lg" />
          <Text size="100" color="subtle">{a.label}</Text>
        </Flex>
      ))}
    </Flex>
  );
}

function AvatarStackBlock() {
  return (
    <Flex gap={3} alignItems="center">
      <AvatarGroup
        accessibilityLabel="Collaborators nearby"
        collaborators={[{ name: "Ava" }, { name: "Ravi" }, { name: "Mei" }, { name: "Leo" }, { name: "Zoe" }]}
        size="md"
      />
      <Text size="100" color="subtle">128 collaborators active near you</Text>
    </Flex>
  );
}

function TestimonialBlock() {
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={360}>
      <Box marginBottom={2}>
        <Flex gap={1}>
          {[0, 1, 2, 3].map((i) => <Icon key={i} icon="star" accessibilityLabel="" color="warning" size={16} />)}
          <Icon icon="star-half" accessibilityLabel="4.5 out of 5 stars" color="warning" size={16} />
        </Flex>
      </Box>
      <Text italic>"Booked Bloom Studio through MusicLinkd and the Atmos room specs were exactly as listed. Smooth session."</Text>
      <Box marginTop={3}>
        <Flex gap={2} alignItems="center">
          <Avatar name="Jordan Blake" size="sm" />
          <Box>
            <Text size="100" weight="bold">Jordan Blake</Text>
            <Text size="100" color="subtle">Independent producer</Text>
          </Box>
        </Flex>
      </Box>
    </Box>
  );
}

function PricingPlansBlock() {
  const plans = [
    { name: "Free", price: "₹0", features: ["3 uploads/mo", "Standard support"], highlight: false },
    { name: "Pro", price: "₹499/mo", features: ["Unlimited uploads", "Advanced analytics", "Priority support"], highlight: true },
  ];
  return (
    <Flex gap={3} wrap>
      {plans.map((p) => (
        <Box key={p.name} borderStyle="sm" rounding={4} padding={5} width={180} color={p.highlight ? "infoWeak" : "default"}>
          <Text weight="bold">{p.name}</Text>
          <Box marginTop={1} marginBottom={3}><Heading size="400">{p.price}</Heading></Box>
          <Flex direction="column" gap={1}>
            {p.features.map((f) => (
              <Flex key={f} gap={1} alignItems="center">
                <Icon icon="check-circle-fill" accessibilityLabel="" size={12} color={p.highlight ? "info" : "success"} />
                <Text size="100">{f}</Text>
              </Flex>
            ))}
          </Flex>
          <Box marginTop={3}><Button text={p.highlight ? "Upgrade" : "Current plan"} color={p.highlight ? "red" : "gray"} fullWidth /></Box>
        </Box>
      ))}
    </Flex>
  );
}

// ---------------------------------------------------------------------------
// The following blocks are validated against the real per-screen registry at
// dist/preview/flows/**/screen.json (124 real screens, audited this
// session) rather than assumed from the old styleguide's nav list. Where a
// screen's dataContract includes a real seed sample, that sample's actual
// values are used (not invented placeholders) — e.g. Booking Summary below
// uses the literal studio/date/amount from booking_summary/screen.json.
// ---------------------------------------------------------------------------

// dist/preview/flows/booking/booking_summary/screen.json — real dataContract
// sample: studio "Yash Raj Sound Studios", 2026-08-12, 10:00 AM–2:00 PM,
// ₹32,000, recording session, half-day, engineer requested, paid via UPI.
function BookingSummaryBlock() {
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={340}>
      <Flex justifyContent="between" alignItems="center">
        <Text weight="bold">Booking summary</Text>
        <Status type="ok" title="Confirmed" />
      </Flex>
      <Box marginTop={3} marginBottom={3}><Divider /></Box>
      <Flex direction="column" gap={2}>
        <Flex justifyContent="between"><Text size="100" color="subtle">Studio</Text><Text size="100">Yash Raj Sound Studios</Text></Flex>
        <Flex justifyContent="between"><Text size="100" color="subtle">Date</Text><Text size="100">Aug 12, 2026</Text></Flex>
        <Flex justifyContent="between"><Text size="100" color="subtle">Slot</Text><Text size="100">10:00 AM – 2:00 PM</Text></Flex>
        <Flex justifyContent="between"><Text size="100" color="subtle">Session</Text><Text size="100">Recording · Half-day · +Engineer</Text></Flex>
        <Flex justifyContent="between"><Text size="100" color="subtle">Payment</Text><Badge text="Paid via UPI" type="success" /></Flex>
      </Flex>
      <Box marginTop={3} marginBottom={2}><Divider /></Box>
      <Flex justifyContent="between"><Text weight="bold">Total</Text><Text weight="bold">₹32,000</Text></Flex>
    </Box>
  );
}

// dist/preview/flows/booking/availability_calendar/screen.json — real
// states: monthly/weekly/timeline. Slot status derives from querying
// existing bookings (available/booked/pending), per that screen's notes.
function AvailabilityCalendarBlock() {
  const [view, setView] = useState<"monthly" | "weekly" | "timeline">("weekly");
  const slots = [
    { time: "9:00", status: "available" as const },
    { time: "10:00", status: "booked" as const },
    { time: "11:00", status: "booked" as const },
    { time: "12:00", status: "pending" as const },
    { time: "13:00", status: "available" as const },
    { time: "14:00", status: "available" as const },
  ];
  const statusColor = { available: "successWeak", booked: "errorWeak", pending: "warningWeak" } as const;
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={340}>
      {/* Was a single justifyContent="between" row (label + 3-segment control
          side by side) — inside this card's 340px maxWidth that left the
          SegmentedControl only ~150-180px, too narrow for "monthly" /
          "weekly" / "timeline" side by side, so their own labels wrapped
          mid-word ("timelin" / "e"). Stacked instead so the control gets
          the card's full inner width on its own row. */}
      <Box marginBottom={3}>
        <Box marginBottom={2}><Text weight="bold">Availability</Text></Box>
        <SegmentedControl items={["monthly", "weekly", "timeline"]} selectedItemIndex={["monthly", "weekly", "timeline"].indexOf(view)} onChange={({ activeIndex }) => setView(["monthly", "weekly", "timeline"][activeIndex] as typeof view)} />
      </Box>
      <Flex direction="column" gap={1}>
        {slots.map((s) => (
          <Flex key={s.time} gap={2} alignItems="center">
            <Box width={48}><Text size="100" color="subtle">{s.time}</Text></Box>
            <Box flex="grow" height={20} rounding={1} color={statusColor[s.status]} display="flex" alignItems="center" paddingX={2}>
              <Text size="100">{s.status}</Text>
            </Box>
          </Flex>
        ))}
      </Flex>
    </Box>
  );
}

// dist/preview/flows/communication/chat_thread_list + chat_conversation —
// real screens (flowStatus "partial", collectionsTouched: chat_threads).
function ChatBlock() {
  const threads = [
    { name: "Ravi Nair", preview: "Sent you the stems", time: "2m" },
    { name: "Bloom Studio A", preview: "Booking confirmed for Aug 12", time: "1h" },
  ];
  const messages = [
    { from: "them", text: "Hey! Can you send the reference track?" },
    { from: "me", text: "Sure, uploading now." },
    { from: "them", text: "Perfect, thanks 🙏" },
  ];
  return (
    <Flex gap={3} wrap>
      <Box borderStyle="sm" rounding={3} padding={3} width={160}>
        <Text size="100" weight="bold">Threads</Text>
        <Box marginTop={2}>
          <Flex direction="column" gap={2}>
            {threads.map((t) => (
              <Flex key={t.name} gap={2} alignItems="center">
                <Avatar name={t.name} size="sm" />
                <Box overflow="hidden">
                  <Text size="100" weight="bold">{t.name}</Text>
                  <Text size="100" color="subtle">{t.preview}</Text>
                </Box>
              </Flex>
            ))}
          </Flex>
        </Box>
      </Box>
      <Box borderStyle="sm" rounding={3} padding={3} width={200}>
        <Flex direction="column" gap={2}>
          {messages.map((m, i) => (
            <Box key={i} alignSelf={m.from === "me" ? "end" : "start"} maxWidth={160} rounding={3} paddingX={2} paddingY={1} color={m.from === "me" ? "infoWeak" : "secondary"}>
              <Text size="100">{m.text}</Text>
            </Box>
          ))}
        </Flex>
      </Box>
    </Flex>
  );
}

// dist/preview/flows/reviews/rating_breakdown/screen.json — real screen
// (ReputationSubScreen.RATING_BREAKDOWN, aggregate score + category
// breakdown).
function RatingBreakdownBlock() {
  const categories = [
    { label: "Communication", value: 4.8 },
    { label: "Punctuality", value: 4.6 },
    { label: "Sound quality", value: 4.9 },
  ];
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={300}>
      <Box marginBottom={3}>
        <Flex gap={3} alignItems="center">
          <Heading size="600">4.8</Heading>
          <Box>
            <Flex gap={1}>{[0, 1, 2, 3].map((i) => <Icon key={i} icon="star" accessibilityLabel="" color="warning" size={14} />)}<Icon icon="star-half" accessibilityLabel="" color="warning" size={14} /></Flex>
            <Text size="100" color="subtle">128 reviews</Text>
          </Box>
        </Flex>
      </Box>
      <Flex direction="column" gap={2}>
        {categories.map((c) => (
          <Flex key={c.label} justifyContent="between" alignItems="center">
            <Text size="100" color="subtle">{c.label}</Text>
            <Box width={100} height={6} rounding={1} color="secondary" overflow="hidden">
              <Box height={6} color="warning" dangerouslySetInlineStyle={{ __style: { width: `${(c.value / 5) * 100}%` } }} />
            </Box>
            <Text size="100">{c.value}</Text>
          </Flex>
        ))}
      </Flex>
    </Box>
  );
}

// dist/preview/flows/reviews/write_review/screen.json — real states:
// default/star_selected/subcategory_ratings/comment_entered/submitted.
function WriteReviewBlock() {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={320}>
      <Text weight="bold">Rate your session</Text>
      <Box marginTop={2} marginBottom={3}>
        <Flex gap={1}>
          {[1, 2, 3, 4, 5].map((n) => (
            <TapArea key={n} onTap={() => setRating(n)} accessibilityLabel={`${n} stars`}>
              <Icon icon={n <= rating ? "star" : "star-outline"} accessibilityLabel="" color={n <= rating ? "warning" : "subtle"} size={22} />
            </TapArea>
          ))}
        </Flex>
      </Box>
      <TextArea id="block-write-review" placeholder="How was the session?" value={comment} onChange={({ value }) => setComment(value)} />
      <Box marginTop={3}><Button text="Submit review" color="red" disabled={rating === 0} onClick={() => {}} /></Box>
    </Box>
  );
}

// dist/preview/flows/portfolio/success/screen.json — real "Success State"
// screen (flowStatus app-mocked).
function SuccessStateBlock() {
  return (
    <Box borderStyle="sm" rounding={4} padding={6} maxWidth={300} display="flex" direction="column" alignItems="center">
      <Box color="successWeak" rounding="circle" padding={4}>
        <Icon icon="check-circle-fill" accessibilityLabel="" color="success" size={32} />
      </Box>
      <Box marginTop={3} marginBottom={1}><Heading size="300" align="center">Booking confirmed</Heading></Box>
      <Text align="center" color="subtle">You're all set for Aug 12 at Yash Raj Sound Studios.</Text>
      <Box marginTop={4}><Button text="View booking" color="red" onClick={() => {}} /></Box>
    </Box>
  );
}

// dist/preview/flows/qr_share/qr_share_flow/screen.json — real screen, but
// flowStatus is "missing": the UI exists (QrAndShareFlow, SupportingScreens.kt:655)
// while the backend doesn't (no schema, simulated PDF, no real QR payload).
// Building the UI honestly, labeled with that real status rather than
// implying it's fully wired.
function QrShareBlock() {
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={280} display="flex" direction="column" alignItems="center">
      <Badge text="UI only — backend missing" type="warning" />
      <Box marginTop={3} marginBottom={3} padding={3} borderStyle="sm" rounding={2}>
        <Icon icon="qr-code" accessibilityLabel="QR code" size={96} />
      </Box>
      <Text weight="bold" align="center">Share your Comp Card</Text>
      <Text size="100" color="subtle" align="center">Scan to view profile & portfolio</Text>
      <Box marginTop={3}><Button text="Download PDF" color="gray" onClick={() => {}} /></Box>
    </Box>
  );
}

// dist/preview/flows/portfolio/credit_attribution/screen.json — real
// "Credit Attribution" screen (flowStatus app-mocked, part of Add Portfolio flow).
function CreditAttributionBlock() {
  const credits = [
    { name: "Ravi Nair", role: "Producer" },
    { name: "Mei Tanaka", role: "Mix engineer" },
    { name: "Ava Chen", role: "Vocalist" },
  ];
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={300}>
      <Text weight="bold">Credits</Text>
      <Box marginTop={3}>
        <Flex direction="column" gap={2}>
          {credits.map((c) => (
            <Flex key={c.name} gap={2} alignItems="center" justifyContent="between">
              <Flex gap={2} alignItems="center">
                <Avatar name={c.name} size="sm" />
                <Text size="100">{c.name}</Text>
              </Flex>
              <Badge text={c.role} type="neutral" />
            </Flex>
          ))}
        </Flex>
      </Box>
      <Box marginTop={3}><Button text="Add collaborator" color="gray" size="sm" onClick={() => {}} /></Box>
    </Box>
  );
}

// Real tab titles pulled from actual profile-flow screens: "Services Tab",
// "Team & Works Tab", "Reviews & Gallery Tab", "Equipment Tab" all exist as
// real screens in dist/preview/flows/business_profiles/*. This is Gestalt's
// real Tabs component, applied to the real section names MusicLinkd uses.
function SectionTabsBlock() {
  const tabs = ["Overview", "Services", "Team & Works", "Reviews & Gallery"];
  const [active, setActive] = useState(0);
  return (
    <Box borderStyle="sm" rounding={3} padding={2} maxWidth={340}>
      {/* Tabs' own flex row doesn't wrap, and 4 real section names (incl.
          "Team & Works" / "Reviews & Gallery") need more than this card's
          ~300px inner width — without a scroll boundary the overflow was
          rendering past this card's right edge, on top of whatever card
          sits next to it. overflow-x:auto keeps it inside this card and
          scrollable instead of bleeding into the neighbor. */}
      <Box overflow="scrollX">
        <Tabs
          activeTabIndex={active}
          onChange={({ activeTabIndex }) => setActive(activeTabIndex)}
          tabs={tabs.map((t, i) => ({ href: "#", text: t, id: `sec-tab-${i}` }))}
        />
      </Box>
      <Box padding={3}><Text size="100" color="subtle">{tabs[active]} content goes here.</Text></Box>
    </Box>
  );
}

// ---------------------------------------------------------------------------
// Admin-perspective blocks — the admin panel's own real patterns (DataTable,
// sidebar navigation, top bar), built from real Gestalt primitives.
// ---------------------------------------------------------------------------

function AdminDataTableBlock() {
  const [sortCol, setSortCol] = useState<"name" | "status">("name");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selected, setSelected] = useState<Set<string>>(new Set());
  const rows = [
    { id: "1", name: "Ava Chen", role: "Talent", status: "Active" },
    { id: "2", name: "Bloom Studio A", role: "Studio", status: "Pending" },
    { id: "3", name: "Ravi Nair", role: "Talent", status: "Active" },
  ];
  const toggle = (id: string) => setSelected((s) => { const n = new Set(s); n.has(id) ? n.delete(id) : n.add(id); return n; });
  return (
    <Box borderStyle="sm" rounding={3} padding={3} maxWidth={420}>
      <Table accessibilityLabel="Admin users table">
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell><Text weight="bold" size="100"> </Text></Table.HeaderCell>
            <Table.SortableHeaderCell
              status={sortCol === "name" ? "active" : "inactive"}
              sortOrder={sortOrder}
              onSortChange={() => { setSortCol("name"); setSortOrder((o) => (o === "asc" ? "desc" : "asc")); }}
            >
              <Text weight="bold" size="100">Name</Text>
            </Table.SortableHeaderCell>
            <Table.HeaderCell><Text weight="bold" size="100">Role</Text></Table.HeaderCell>
            <Table.SortableHeaderCell
              status={sortCol === "status" ? "active" : "inactive"}
              sortOrder={sortOrder}
              onSortChange={() => { setSortCol("status"); setSortOrder((o) => (o === "asc" ? "desc" : "asc")); }}
            >
              <Text weight="bold" size="100">Status</Text>
            </Table.SortableHeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {rows.map((r) => (
            <Table.Row key={r.id}>
              <Table.Cell><Checkbox id={`row-${r.id}`} checked={selected.has(r.id)} onChange={() => toggle(r.id)} /></Table.Cell>
              <Table.Cell><Text size="100">{r.name}</Text></Table.Cell>
              <Table.Cell><Text size="100" color="subtle">{r.role}</Text></Table.Cell>
              <Table.Cell><Status type={r.status === "Active" ? "ok" : "inProgress"} title={r.status} /></Table.Cell>
            </Table.Row>
          ))}
        </Table.Body>
      </Table>
      <Box marginTop={3}>
        <Flex justifyContent="between" alignItems="center">
          <Text size="100" color="subtle">{selected.size} selected · {rows.length} rows</Text>
          <Flex gap={1}>
            <IconButton icon="arrow-back" accessibilityLabel="Previous page" size="sm" />
            <IconButton icon="arrow-forward" accessibilityLabel="Next page" size="sm" />
          </Flex>
        </Flex>
      </Box>
    </Box>
  );
}

function AdminSidebarBlock() {
  return (
    <Box maxHeight={240} overflow="scroll" maxWidth={220} borderStyle="sm" rounding={2} padding={2}>
      <SideNavigation accessibilityLabel="Admin navigation">
        <SideNavigation.TopItem href="#" label="Dashboard" icon="gauge" active="page" />
        <SideNavigation.TopItem href="#" label="Users" icon="person" />
        <SideNavigation.TopItem href="#" label="Bookings" icon="calendar" />
        <SideNavigation.Section label="Content">
          <SideNavigation.TopItem href="#" label="Widgets" icon="apps" />
          <SideNavigation.TopItem href="#" label="Flows & Screens" icon="layout" />
        </SideNavigation.Section>
        <SideNavigation.TopItem href="#" label="Settings" icon="cog" />
      </SideNavigation>
    </Box>
  );
}

function AdminTopBarBlock() {
  const [query, setQuery] = useState("");
  return (
    <Box borderStyle="sm" rounding={3} padding={3}>
      <Flex gap={3} alignItems="center">
        <Text weight="bold">Admin</Text>
        <Badge text="v5.5" type="neutral" />
        <Box flex="grow" maxWidth={220}>
          <SearchField id="block-admin-search" accessibilityLabel="Search admin" placeholder="Search users, bookings…" value={query} onChange={({ value }) => setQuery(value)} />
        </Box>
        <IconButton icon="bell" accessibilityLabel="Notifications" size="sm" />
        <IconButton icon="cog" accessibilityLabel="Settings" size="sm" />
        <Avatar name="Admin User" size="sm" />
      </Flex>
    </Box>
  );
}

type Block = { name: string; description: string; render: () => React.ReactNode };

// dist/preview/flows/public_profile/public_profile_view_venue/screen.json —
// the same real dataContract sample now actually designed and deployed to
// production at admin.musiclinkd.com/preview/flows/public_profile/
// public_profile_view_venue/ (2026-08-16), not just cataloged. Ported here
// verbatim rather than re-derived, so this card and the live webapp screen
// stay the same real screen, not two independent guesses at it.
//
// Same honest gap carried over from that build: this screen's own notes
// field says "profileType === VENUE — renders the Venue extension fields,"
// but its actual dataContract sample is Talent-shaped (vocalRange,
// sessionAvailability) — no Venue fields (capacity/address/amenities etc.)
// exist anywhere in this build to render instead. Shown as the real sample
// given, not backfilled with invented Venue fields.
function PublicProfileVenueBlock() {
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={360}>
      <Flex justifyContent="between" alignItems="center">
        <IconButton icon="arrow-back" accessibilityLabel="Back" size="sm" />
        <IconButton icon="share" accessibilityLabel="Share" size="sm" />
      </Flex>
      <Box marginTop={2} marginBottom={3}>
        <Flex direction="column" alignItems="center" gap={2}>
          <Avatar name="Arijit Sen" src="https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400" size="xl" />
          <Flex gap={1} alignItems="center">
            <Text weight="bold" size="400">Arijit Sen</Text>
            <Status type="ok" title="Verified" />
          </Flex>
          <Text size="100" color="subtle" align="center">Singer · Composer · Mumbai, MH · Andheri</Text>
        </Flex>
      </Box>
      <Box marginBottom={3}>
        <Flex gap={1} wrap justifyContent="center">
          <Tag text="Verified Artist" />
          <Tag text="Rising Talent" />
          <Tag text="Available for Collab" />
          <Tag text="Touring" />
        </Flex>
      </Box>
      <Text size="100" align="center">
        Playback &amp; classical singer known for Bollywood, Sufi, and acoustic reinterpretations. 10+ years touring and session work.
      </Text>
      <Box marginTop={3} marginBottom={3}>
        <Flex gap={1} wrap justifyContent="center">
          {["Bollywood / Filmi", "Sufi Devotional", "Acoustic / Unplugged", "Acoustic Guitar", "Hindi", "Bengali", "English"].map((t) => (
            <Tag key={t} text={t} onRemove={() => {}} />
          ))}
        </Flex>
      </Box>
      <Box marginBottom={2}><Divider /></Box>
      <Box marginBottom={3}>
        <Text size="100" weight="bold" color="subtle">PORTFOLIO</Text>
        <Box marginTop={1}>
          <Flex justifyContent="between" alignItems="start" gap={2}>
            <Box>
              <Text weight="bold" size="100">Tum Hi Toh (Unplugged)</Text>
              <Text size="100" color="subtle">Acoustic unplugged reinterpretation — 4.2M plays on Spotify.</Text>
            </Box>
            <Badge text="Released" type="neutral" />
          </Flex>
          <Box marginTop={1}><Text size="100" color="subtle">4.2K views · 610 likes · 88 shares</Text></Box>
        </Box>
      </Box>
      <Box marginBottom={3}>
        <Text size="100" weight="bold" color="subtle">REVIEWS</Text>
        <Box marginTop={1}>
          <Text size="200">★★★★★</Text>
          <Text size="100">&quot;Exceptional range and control across genres.&quot;</Text>
          <Text size="100" color="subtle">— T-Series A&amp;R Team</Text>
        </Box>
      </Box>
      <Button text="Contact Arijit" color="red" fullWidth />
    </Box>
  );
}

// The following 8 Blocks render real entries from
// admin/app/(admin)/widgets/default-widgets.ts (backed by
// admin/docs/architecture/Widget_Directory.md) as actual visual Gestalt
// components, not just the read-only spec text already shown in the
// "Admin widget directory" tab below. Field values (labels, copy) are
// illustrative sample data — the widget's PURPOSE and CONTENT COMPOSITION
// fields are the real, checked source; none of these were skipped or
// guessed past that. Deliberately not rebuilding BookingCard (already
// real as "Booking summary" above) or a plain track row (already real as
// "Track row" above, just missing artwork/platform badges that this
// version of the card includes) — this is additive, not duplicative.

// UnifiedTrackCard (Widget_Directory §5) — artwork + platform badges,
// distinct form factor from the existing horizontal "Track row".
function TrackCardBlock() {
  return (
    <Box borderStyle="sm" rounding={3} padding={3} maxWidth={220}>
      <Box rounding={2} overflow="hidden" marginBottom={2}>
        <Image alt="Track artwork" src={placeholderImg("#8B5CF6", "Track", 220, 220)} naturalWidth={220} naturalHeight={220} />
      </Box>
      <Text weight="bold" size="100">Neon Tide</Text>
      <Text size="100" color="subtle">Mei Tanaka · 3:42</Text>
      <Box marginTop={2} marginBottom={2}>
        <Flex gap={1}>
          <Badge text="Spotify" type="success" />
          <Badge text="YouTube" type="error" />
        </Flex>
      </Box>
      <Flex gap={1} alignItems="center">
        <Icon icon="music-on" accessibilityLabel="" size={12} color="subtle" />
        <Text size="100" color="subtle">482K plays</Text>
      </Flex>
    </Box>
  );
}

// UnifiedStudioCard (Widget_Directory §2)
function StudioCardBlock() {
  return (
    <Box borderStyle="sm" rounding={3} padding={3} maxWidth={260}>
      <Box rounding={2} overflow="hidden" marginBottom={2}>
        <Image alt="Studio room" src={placeholderImg("#0D0B26", "Studio", 260, 150)} naturalWidth={260} naturalHeight={150} />
      </Box>
      <Flex justifyContent="between" alignItems="start">
        <Text weight="bold" size="200">Bloom Studio A</Text>
        <Badge text="Dolby Atmos" type="info" />
      </Flex>
      <Flex gap={1} alignItems="center">
        <Icon icon="location" accessibilityLabel="" size={12} color="subtle" />
        <Text size="100" color="subtle">Bandra, Mumbai</Text>
      </Flex>
      <Box marginTop={2}>
        <Flex justifyContent="between" alignItems="center">
          <Flex gap={1} alignItems="center">
            <Icon icon="star" accessibilityLabel="" size={12} color="warning" />
            <Text size="100">4.8 (126)</Text>
          </Flex>
          <Text weight="bold" size="100">₹2,500/hr</Text>
        </Flex>
      </Box>
    </Box>
  );
}

// FeaturedStudioCard (Widget_Directory §2) — hero promotion banner variant,
// distinct from the standard StudioCard above.
function FeaturedStudioCardBlock() {
  return (
    <Box borderStyle="sm" rounding={4} overflow="hidden" maxWidth={360}>
      <Image alt="Featured studio" src={placeholderImg("#EA4C89", "Featured Studio", 360, 180)} naturalWidth={360} naturalHeight={180} />
      <Box padding={4}>
        <Text weight="bold" size="300">Yash Raj Sound Studios</Text>
        <Text size="100" color="subtle">Room B · SSL 9000J console · Dolby Atmos 7.1.4</Text>
        <Box marginTop={3}>
          <Flex justifyContent="between" alignItems="center">
            <Text weight="bold">₹8,000/hr</Text>
            <Button text="Book now" color="red" size="sm" />
          </Flex>
        </Box>
      </Box>
    </Box>
  );
}

// UnifiedMarketplaceCard (Widget_Directory §3)
function MarketplaceCardBlock() {
  return (
    <Box borderStyle="sm" rounding={3} padding={3} maxWidth={220}>
      <Box rounding={2} overflow="hidden" marginBottom={2}>
        <Image alt="Gear photo" src={placeholderImg("#F59E0B", "Gear", 220, 160)} naturalWidth={220} naturalHeight={160} />
      </Box>
      <Flex justifyContent="between" alignItems="start">
        <Text weight="bold" size="100">Neumann U87 Mic</Text>
        <Badge text="Available" type="success" />
      </Flex>
      <Text size="100" color="subtle">₹800/day rental</Text>
      <Box marginTop={1}>
        <Flex gap={1} alignItems="center">
          <Icon icon="star" accessibilityLabel="" size={12} color="warning" />
          <Text size="100" color="subtle">4.6 seller rating</Text>
        </Flex>
      </Box>
    </Box>
  );
}

// UnifiedOpportunityCard (Widget_Directory §3)
function OpportunityCardBlock() {
  return (
    <Box borderStyle="sm" rounding={3} padding={4} maxWidth={340}>
      <Flex justifyContent="between" alignItems="start">
        <Text weight="bold">Session vocalist for indie EP</Text>
        <Tag text="₹15,000 budget" />
      </Flex>
      <Text size="100" color="subtle">Posted by Ravi Nair · Producer</Text>
      <Box marginTop={2} marginBottom={2}>
        <Flex gap={1} wrap>
          <Tag text="Playback singing" />
          <Tag text="Hindi" />
          <Tag text="Studio session" />
        </Flex>
      </Box>
      <Flex justifyContent="between" alignItems="center">
        <Flex gap={1} alignItems="center">
          <Icon icon="people" accessibilityLabel="" size={12} color="subtle" />
          <Text size="100" color="subtle">12 applicants</Text>
        </Flex>
        <Flex gap={1} alignItems="center">
          <Icon icon="calendar" accessibilityLabel="" size={12} color="subtle" />
          <Text size="100" color="subtle">Deadline Aug 24</Text>
        </Flex>
      </Flex>
    </Box>
  );
}

// UnifiedReviewCard (Widget_Directory §7) — adds organization + timestamp
// vs. the existing Testimonial block (which has role, not org, and no
// timestamp).
function ReviewCardBlock() {
  return (
    <Box borderStyle="sm" rounding={3} padding={4} maxWidth={340}>
      <Flex gap={2} alignItems="center">
        <Avatar name="Priya Menon" size="sm" />
        <Box>
          <Text weight="bold" size="100">Priya Menon</Text>
          <Text size="100" color="subtle">A&amp;R, Zee Music Company</Text>
        </Box>
      </Flex>
      <Box marginTop={2} marginBottom={1}>
        <Flex gap={1}>
          {[0, 1, 2, 3, 4].map((i) => <Icon key={i} icon="star" accessibilityLabel={i === 4 ? "5 out of 5 stars" : ""} color="warning" size={14} />)}
        </Flex>
      </Box>
      <Text size="100">&quot;Delivered mix stems ahead of schedule, communicated clearly throughout.&quot;</Text>
      <Box marginTop={2}><Text size="100" color="subtle">3 days ago</Text></Box>
    </Box>
  );
}

// UnifiedNotificationCard (Widget_Directory §7) — per-item event-type icon,
// distinct from the existing plain-text "Activity feed" block.
function NotificationCardBlock() {
  const items: { icon: "speech" | "calendar-check" | "people"; label: string; who: string; detail: string; time: string; unread: boolean }[] = [
    { icon: "speech", label: "Comment", who: "Ravi Nair", detail: 'commented on "Neon Tide"', time: "2m", unread: true },
    { icon: "calendar-check", label: "Booking", who: "Bloom Studio A", detail: "confirmed your session", time: "1h", unread: true },
    { icon: "people", label: "Follow", who: "Ava Chen", detail: "started following you", time: "3h", unread: false },
  ];
  return (
    <Flex direction="column" gap={2}>
      {items.map((it, i) => (
        <Box key={i} borderStyle="sm" rounding={2} padding={3} color={it.unread ? "infoWeak" : undefined}>
          <Flex gap={2} alignItems="start">
            <Avatar name={it.who} size="sm" />
            <Box flex="grow">
              <Text size="100"><Text inline weight="bold" size="100">{it.who}</Text> {it.detail}</Text>
              <Flex gap={1} alignItems="center">
                <Icon icon={it.icon} accessibilityLabel={it.label} size={10} color="subtle" />
                <Text size="100" color="subtle">{it.time} ago</Text>
              </Flex>
            </Box>
            {it.unread && <Status type="unstarted" title="Unread" />}
          </Flex>
        </Box>
      ))}
    </Flex>
  );
}

// PublicReputationWidget (Widget_Directory §7) — aggregated stat card,
// distinct from the badge-chip-row "Trust badges" block above.
function PublicReputationWidgetBlock() {
  return (
    <Box borderStyle="sm" rounding={4} padding={5} maxWidth={320}>
      <Flex alignItems="center" gap={3}>
        <Text weight="bold" size="600">4.9</Text>
        <Box>
          <Flex gap={1}>
            {[0, 1, 2, 3, 4].map((i) => <Icon key={i} icon="star" accessibilityLabel="" color="warning" size={12} />)}
          </Flex>
          <Text size="100" color="subtle">from 214 reviews</Text>
        </Box>
      </Flex>
      <Box marginTop={3}><Divider /></Box>
      <Box marginTop={3}>
        <Flex justifyContent="between">
          <Box>
            <Text weight="bold">38</Text>
            <Text size="100" color="subtle">Verified credits</Text>
          </Box>
          <Box>
            <Text weight="bold">98%</Text>
            <Text size="100" color="subtle">Response rate</Text>
          </Box>
        </Flex>
      </Box>
    </Box>
  );
}

const BLOCKS: Block[] = [
  { name: "Navigation header", description: "Brand + real SearchField + notifications + avatar.", render: () => <NavHeaderBlock /> },
  { name: "Artist profile card", description: "Avatar, tags, and a real toggling Follow button.", render: () => <ArtistProfileBlock /> },
  { name: "Track row", description: "Play/pause toggles the icon and status live.", render: () => <TrackRowBlock /> },
  { name: "Upload release form", description: "TextField + SelectList + Checkbox, button disables until valid.", render: () => <UploadFormBlock /> },
  { name: "Stats row", description: "Three Datapoints with real trend arrows.", render: () => <StatsRowBlock /> },
  { name: "Empty state", description: "Icon + heading + CTA for a catalog with nothing in it yet.", render: () => <EmptyStateBlock /> },
  { name: "Notification stack", description: "Three real BannerSlim variants stacked.", render: () => <NotificationStackBlock /> },
  { name: "Cover art gallery", description: "Real Masonry layout (Pinterest's own algorithm) packing variable-height tiles.", render: () => <GalleryBlock /> },
  { name: "Bottom navigation bar", description: "Mobile tab bar — real TapArea + Icon + Text, live active-tab state.", render: () => <BottomNavBlock /> },
  { name: "Trust badges", description: "Real tiers from admin/components/shared/trust-badge.tsx, mapped to Gestalt Badge + Icon.", render: () => <TrustBadgesBlock /> },
  { name: "Hero", description: "Gradient hero banner — real brand colors, Box + Heading + Text + Button.", render: () => <HeroBlock /> },
  { name: "Metrics scorecard", description: "Grid of real Datapoints with icons and trend arrows.", render: () => <MetricsScorecardBlock /> },
  { name: "Activity feed", description: "Avatar + text feed rows, real components.", render: () => <ActivityFeedBlock /> },
  { name: "Search & filters", description: "Real SearchField + Dropdown + removable Tag pills.", render: () => <SearchAndFiltersBlock /> },
  { name: "Quick actions", description: "Row of real IconButtons with labels.", render: () => <QuickActionsBlock /> },
  { name: "Avatar stack / social proof", description: "Real AvatarGroup with a count.", render: () => <AvatarStackBlock /> },
  { name: "Testimonial", description: "Star rating (real Icon) + quote + real Avatar.", render: () => <TestimonialBlock /> },
  { name: "Pricing plans", description: "Two real Box cards with feature lists and a highlighted plan.", render: () => <PricingPlansBlock /> },
  { name: "Booking summary", description: "Real data from booking_summary/screen.json — Yash Raj Sound Studios, Aug 12, ₹32,000.", render: () => <BookingSummaryBlock /> },
  { name: "Availability calendar", description: "Real states from availability_calendar/screen.json — monthly/weekly/timeline, live SegmentedControl.", render: () => <AvailabilityCalendarBlock /> },
  { name: "Chat", description: "Thread list + conversation bubbles — real screens chat_thread_list + chat_conversation.", render: () => <ChatBlock /> },
  { name: "Rating breakdown", description: "Aggregate score + category bars — real screen rating_breakdown.", render: () => <RatingBreakdownBlock /> },
  { name: "Write review", description: "Live star selector + comment — real states from write_review/screen.json.", render: () => <WriteReviewBlock /> },
  { name: "Success state", description: "Confirmation screen — real screen portfolio/success.", render: () => <SuccessStateBlock /> },
  { name: "QR share", description: "Real screen (qr_share_flow) — flowStatus \"missing\": UI exists, backend doesn't. Labeled honestly.", render: () => <QrShareBlock /> },
  { name: "Credit attribution", description: "Collaborator credits list — real screen credit_attribution.", render: () => <CreditAttributionBlock /> },
  { name: "Section tabs", description: "Real Gestalt Tabs over real section names from business-profile screens.", render: () => <SectionTabsBlock /> },
  { name: "Public profile", description: "Real screen public_profile_view_venue — same data as the now-live webapp screen at admin.musiclinkd.com/preview.", render: () => <PublicProfileVenueBlock /> },
  { name: "Track card", description: "UnifiedTrackCard (Widget Directory §5) — artwork, platform badges, play count.", render: () => <TrackCardBlock /> },
  { name: "Studio card", description: "UnifiedStudioCard (Widget Directory §2) — imagery, Atmos badge, rating, hourly rate.", render: () => <StudioCardBlock /> },
  { name: "Featured studio card", description: "FeaturedStudioCard (Widget Directory §2) — hero promotion banner variant.", render: () => <FeaturedStudioCardBlock /> },
  { name: "Marketplace card", description: "UnifiedMarketplaceCard (Widget Directory §3) — gear rental listing.", render: () => <MarketplaceCardBlock /> },
  { name: "Opportunity card", description: "UnifiedOpportunityCard (Widget Directory §3) — gig/job/collab posting.", render: () => <OpportunityCardBlock /> },
  { name: "Review card", description: "UnifiedReviewCard (Widget Directory §7) — reviewer, org, stars, quote, timestamp.", render: () => <ReviewCardBlock /> },
  { name: "Notification card", description: "UnifiedNotificationCard (Widget Directory §7) — per-item event-type icon + unread state.", render: () => <NotificationCardBlock /> },
  { name: "Public reputation widget", description: "PublicReputationWidget (Widget Directory §7) — aggregate rating, credits, response rate.", render: () => <PublicReputationWidgetBlock /> },
  { name: "Admin data table", description: "Real Table.SortableHeaderCell + Checkbox selection + pagination controls.", render: () => <AdminDataTableBlock /> },
  { name: "Admin sidebar", description: "Real SideNavigation themed for the admin panel.", render: () => <AdminSidebarBlock /> },
  { name: "Admin top bar", description: "Real SearchField + IconButtons + Avatar, admin-panel framing.", render: () => <AdminTopBarBlock /> },
];

function visibilityBadge(w: WidgetEntry) {
  if (w.isVisible === false) return <Badge text="Hidden" type="neutral" />;
  if (w.isVisible === true) return <Badge text="Visible" type="success" />;
  return <Badge text="Unconfirmed" type="lightWash" />;
}

// Read-only mirror of admin/app/(admin)/widgets/default-widgets.ts — see
// widgets-mirror.ts for the full provenance note. Rendered as data cards
// (name, purpose, composition, real code-mapping, visibility, notes),
// grouped by the same 9 categories as the source doc, filtered by the same
// search box as the rest of this tab.
function WidgetsDirectory({ query }: { query: string }) {
  const filtered = WIDGETS.filter(
    (w) =>
      w.name.toLowerCase().includes(query.toLowerCase()) ||
      w.category.toLowerCase().includes(query.toLowerCase()) ||
      w.codeMapping.toLowerCase().includes(query.toLowerCase())
  );
  return (
    <Box>
      <Box marginBottom={5} padding={4} rounding={2} color="infoWeak">
        <Text size="200">
          Read-only mirror of <code>admin/app/(admin)/widgets/default-widgets.ts</code> (backed by{" "}
          <code>admin/docs/architecture/Widget_Directory.md</code>) — the real home/dashboard
          content-composition widgets used by the admin panel's widget editor. This is a reference,
          not the source of truth: widget behavior is edited in the admin app; this list gets
          re-transcribed here if that changes, not the other way around. {WIDGETS.length} widgets
          across {WIDGET_CATEGORIES_ORDER.length} categories.
        </Text>
      </Box>
      {WIDGET_CATEGORIES_ORDER.map((cat) => {
        const items = filtered.filter((w) => w.category === cat);
        if (items.length === 0) return null;
        return (
          <Box key={cat} marginBottom={7}>
            <Box marginBottom={3}><Heading size="300">{cat}</Heading></Box>
            <div className="ml-block-grid">
              {items.map((w) => (
                <Box key={w.key} borderStyle="sm" rounding={3} padding={4}>
                  <Flex justifyContent="between" alignItems="start" gap={2}>
                    <Text weight="bold">{w.name}</Text>
                    {visibilityBadge(w)}
                  </Flex>
                  <Box marginTop={1} marginBottom={2}><Text size="100" color="subtle">{w.purpose}</Text></Box>
                  <Box marginBottom={2}><Text size="100">{w.contentComposition}</Text></Box>
                  <Text size="100" color="subtle"><code>{w.codeMapping}</code></Text>
                  {w.notes && (
                    <Box marginTop={2} padding={2} rounding={1} color="warningWeak">
                      <Text size="100">{w.notes}</Text>
                    </Box>
                  )}
                </Box>
              ))}
            </div>
          </Box>
        );
      })}
      {filtered.length === 0 && <Text color="subtle">No widgets match “{query}”.</Text>}
    </Box>
  );
}

// Every one of these was checked against a real source (Gestalt's actual
// export list, the 27-widget admin directory, or the 124-screen
// dist/preview/flows/**/screen.json registry) before being left out — none
// were skipped for lack of time, and none are guesses. Transcribed from the
// audit recorded in frontend/guide/INDEX.md ("Old styleguide.html nav
// audit," 2026-08-16) — re-sync this list by hand if that audit changes.
type NotBuiltGroup = { title: string; reason: string; items: string[] };
const NOT_BUILT: NotBuiltGroup[] = [
  {
    title: "Not available in Gestalt",
    reason:
      'Checked directly against the real installed package’s export list (gestalt/packages/gestalt/src/index.ts) — these components genuinely don’t exist in it. Breadcrumb specifically was checked against PageHeader.d.ts, which has no breadcrumb prop anywhere.',
    items: ["Upload / file-dropzone", "Timeline", "Skeleton Loading", "Progress Bar", "Time Picker", "Date Picker", "Slider", "A distinct Snackbar (Gestalt uses Toast for both)", "Breadcrumb"],
  },
  {
    title: "Different platform — Android native, out of scope",
    reason:
      "These are Android Material 3 native widgets from the old styleguide's “05 Native Components” section. They belong to the real native app (app/, Kotlin), a separate codebase this web install doesn't cover — Gestalt is web-only and can never render these here.",
    items: ["Top App Bar", "Floating Action Button", "Navigation Drawer", "Navigation Rail", "Segmented Buttons", "Progress Indicators", "OTP Input", "Search Suggestions", "Filter Sheet", "Permission Dialog", "Camera Picker", "Audio Recorder", "Audio Scrubber", "Android App Grids", "M3 Navigation Bar"],
  },
  {
    title: "No real backing found",
    reason:
      "Checked against both the 27-widget admin directory and all 124 real screens in dist/preview/flows/**/screen.json (titles, notes, dataContracts) — no screen, note, code reference, or widget entry backs any of these. Not built because there's nothing real to build from, not because they're hard.",
    items: [
      "Skeleton Loaders", "Error Boundary", "Offline Banner", "Shimmer", "Adaptive Banner", "Navbar", "Navbar (Alt)", "Sticky Nav", "Segmented Switch", "Metrics Row", "Uncategorized", "Swipe Deck", "Brief Wizard",
      "Ambient Album Card", "Artist Grid Card", "Artist Row Card", "Typing Indicator", "Attachments", "Notification Toggle (distinct from the real NotificationCard widget)",
      "Multi-Track Stem Mixer", "Equalizer", "User Header", "Talent/Band/Business/Venue as separate card types", "Direct Request", "Request Status Tracker", "Band Card", "Application Card", "Rental Card",
    ],
  },
  {
    title: "Aesthetic-inspiration references, not real screens",
    reason:
      "The old styleguide's “24 Inspired” section was explicit moodboard/inspiration material, not real MusicLinkd screens or components — building these as if they were real product UI would misrepresent what actually exists.",
    items: ["Gradient Hero Cards", "Content Design Patterns", "Artist Cards & Playlist Rows (as a distinct inspired pattern)", "Masonry Card Grid (as inspiration — the real Masonry component itself IS used, in Cover art gallery)", "Full-Bleed Bold Cards"],
  },
];

function NotBuiltRegistry() {
  const total = NOT_BUILT.reduce((n, g) => n + g.items.length, 0);
  return (
    <Box>
      <Box marginBottom={5} padding={4} rounding={2} color="warningWeak">
        <Text size="200">
          {total} items from the old styleguide's nav were checked and left out — every one for a
          specific, checked reason, grouped below. This list exists so "not built" doesn't quietly
          mean "forgotten." Component-level exclusions from the Components tab (Video, Layer,
          Container, provider/hook internals, etc.) are covered separately by the note at the
          bottom of that tab.
        </Text>
      </Box>
      {NOT_BUILT.map((g) => (
        <Box key={g.title} marginBottom={6}>
          <Box marginBottom={1}><Heading size="300">{g.title}</Heading></Box>
          <Box marginBottom={3}><Text size="100" color="subtle">{g.reason}</Text></Box>
          <Flex gap={2} wrap>
            {g.items.map((item) => (
              <Box key={item} borderStyle="sm" rounding={2} padding={2} color="default">
                <Text size="100">{item}</Text>
              </Box>
            ))}
          </Flex>
        </Box>
      ))}
    </Box>
  );
}

const BLOCKS_TABS = ["Composed patterns", "Admin widget directory", "Not built"] as const;

function BlocksGallery({ query }: { query: string }) {
  const [sub, setSub] = useState<(typeof BLOCKS_TABS)[number]>("Composed patterns");
  const filtered = BLOCKS.filter((b) => b.name.toLowerCase().includes(query.toLowerCase()));
  return (
    <Box>
      <div className="ml-token-cat-nav">
        {BLOCKS_TABS.map((t) => (
          <button key={t} className={`ml-token-cat-btn ${sub === t ? "active" : ""}`} onClick={() => setSub(t)}>
            {t}
          </button>
        ))}
      </div>

      {sub === "Composed patterns" && (
        <Box>
          <Box marginBottom={5}>
            <Text color="subtle">
              Composed patterns — each built entirely from the components in the Components tab, arranged the way they'd actually appear in a MusicLinkd screen.
            </Text>
          </Box>
          <div className="ml-block-grid">
            {filtered.map((b) => (
              <Box key={b.name} borderStyle="sm" rounding={4} padding={5}>
                <Box marginBottom={3}>
                  <Text weight="bold">{b.name}</Text>
                  <Text size="100" color="subtle">{b.description}</Text>
                </Box>
                {b.render()}
              </Box>
            ))}
          </div>
          {filtered.length === 0 && <Text color="subtle">No blocks match “{query}”.</Text>}
        </Box>
      )}

      {sub === "Admin widget directory" && <WidgetsDirectory query={query} />}
      {sub === "Not built" && <NotBuiltRegistry />}
    </Box>
  );
}

// ---------------------------------------------------------------------------
// Foundations — the underlying scales (type, spacing, elevation, rounding)
// rendered live from real components/tokens, distinct from the raw token
// dump in the Tokens tab.
// ---------------------------------------------------------------------------

function FoundationsView({ tokenGroups, baseTokenGroups }: { tokenGroups: TokenGroup[]; baseTokenGroups: TokenGroup[] }) {
  // Spacing, rounding, and font-size tokens are theme-invariant — Gestalt's
  // dark-mode CSS only overrides colors (plus a couple of elevation
  // shadows), so those scales are read from the light token set regardless
  // of the current toggle. Elevation is read from whichever theme is active
  // since its shadow values do differ.
  const spacing = baseTokenGroups.find((g) => g.label === "Spacing");
  const rounding = baseTokenGroups.find((g) => g.label === "Rounding");
  const elevation = (tokenGroups.find((g) => g.label === "Elevation")?.tokens.length ? tokenGroups : baseTokenGroups).find((g) => g.label === "Elevation");
  const fontFamily = baseTokenGroups.find((g) => g.label === "Font Family")?.tokens.find((t) => t.name === "--font-family-default-latin");

  // Real recurring borderStyle+rounding combinations, pulled from this
  // app's own Blocks source (grep for `borderStyle="sm"` next to
  // `rounding={n}` in App.tsx) — not official Gestalt style tokens (Gestalt
  // doesn't ship named composite "card style" tokens), just what this app
  // actually uses repeatedly, reported honestly as that.
  const cardRecipes = [
    { label: "Card (large)", borderStyle: "sm" as const, rounding: 4 as const, usedIn: "Artist profile, Upload form, Empty state, Bottom nav" },
    { label: "Card (medium)", borderStyle: "sm" as const, rounding: 3 as const, usedIn: "Track row, Nav header, component cards" },
    { label: "Card (tight)", borderStyle: "sm" as const, rounding: 2 as const, usedIn: "Foundations swatches, token items" },
  ];

  return (
    <Box>
      <Box marginBottom={8}>
        <Box marginBottom={3}><Heading size="300">Typography scale</Heading></Box>
        <Flex direction="column" gap={3}>
          {(["700", "600", "400", "300", "200", "100"] as const).map((size) => (
            <Flex key={size} gap={4} alignItems="baseline">
              <Box width={48}><Text size="100" color="subtle">{size}</Text></Box>
              <Heading size={size}>The quick brown fox</Heading>
            </Flex>
          ))}
        </Flex>
      </Box>

      <Box marginBottom={8}>
        <Box marginBottom={3}><Heading size="300">Rounding</Heading></Box>
        <Flex gap={3} wrap>
          {["0", "1", "2", "3", "4", "circle"].map((r) => (
            <Box key={r} display="flex" direction="column" alignItems="center">
              <Box width={64} height={64} color="secondary" rounding={r === "circle" ? "circle" : (Number(r) as any)} />
              <Text size="100" color="subtle">{r}</Text>
            </Box>
          ))}
        </Flex>
        {rounding && <Box marginTop={2}><Text size="100" color="subtle">{rounding.tokens.length} raw rounding tokens in the Tokens tab.</Text></Box>}
      </Box>

      <Box marginBottom={8}>
        <Box marginBottom={3}><Heading size="300">Elevation</Heading></Box>
        <Flex gap={4} wrap>
          {(elevation?.tokens ?? []).slice(0, 4).map((t) => (
            <Box key={t.name} width={120} height={80} rounding={3} color="default" display="flex" alignItems="center" justifyContent="center" dangerouslySetInlineStyle={{ __style: { boxShadow: `var(${t.name})` } }}>
              <Text size="100" align="center">{t.name.replace("--elevation-", "")}</Text>
            </Box>
          ))}
        </Flex>
      </Box>

      <Box marginBottom={8}>
        <Box marginBottom={3}><Heading size="300">Spacing scale</Heading></Box>
        <Flex direction="column" gap={2}>
          {(spacing?.tokens ?? []).filter((t) => !t.name.includes("negative")).slice(0, 12).map((t) => (
            <Flex key={t.name} gap={3} alignItems="center">
              <Box width={140}><Text size="100" color="subtle">{t.name}</Text></Box>
              <Box height={12} color="brand" dangerouslySetInlineStyle={{ __style: { width: t.value } }} />
              <Text size="100" color="subtle">{t.value}</Text>
            </Flex>
          ))}
        </Flex>
      </Box>

      <Box marginBottom={8}>
        <Box marginBottom={3}>
          <Heading size="300">Styles</Heading>
          <Text size="100" color="subtle">Real composed CSS in use — font stack, observed border widths, and this app's own recurring card recipes — not abstract token names.</Text>
        </Box>

        <Box marginBottom={5}>
          <Text weight="bold" size="100">Font stack</Text>
          <Box marginTop={2} padding={3} rounding={2} borderStyle="sm" dangerouslySetInlineStyle={{ __style: { fontFamily: fontFamily?.value } }}>
            <Text>The quick brown fox jumps over the lazy dog — 0123456789</Text>
          </Box>
          <Box marginTop={1}><Text size="100" color="subtle">{fontFamily ? fontFamily.value : "Not found in shipped CSS."}</Text></Box>
        </Box>

        <Box marginBottom={5}>
          <Text weight="bold" size="100">Border widths (observed)</Text>
          <Box marginTop={2}>
            <Flex gap={4} wrap>
              {BORDER.observedWidths.map((w) => (
                <Flex key={w} direction="column" alignItems="center" gap={1}>
                  <Box width={56} height={56} rounding={2} dangerouslySetInlineStyle={{ __style: { border: `${w} solid var(--color-border-container)` } }} />
                  <Text size="100" color="subtle">{w}</Text>
                </Flex>
              ))}
            </Flex>
          </Box>
          <Box marginTop={1}><Text size="100" color="subtle">{BORDER_GAP}</Text></Box>
        </Box>

        <Box>
          <Text weight="bold" size="100">Card recipes used in this app's Blocks</Text>
          <Box marginTop={2}>
            <Flex gap={3} wrap>
              {cardRecipes.map((r) => (
                <Box key={r.label} width={200} borderStyle={r.borderStyle} rounding={r.rounding} padding={3}>
                  <Text size="100" weight="bold">{r.label}</Text>
                  <Text size="100" color="subtle">borderStyle="{r.borderStyle}" rounding={"{"}{r.rounding}{"}"}</Text>
                  <Box marginTop={1}><Text size="100" color="subtle">{r.usedIn}</Text></Box>
                </Box>
              ))}
            </Flex>
          </Box>
        </Box>
      </Box>

      <Box>
        <Box marginBottom={3}><Heading size="300">Icon sizes</Heading></Box>
        <Flex gap={4} alignItems="center">
          {[12, 16, 24, 32, 48].map((s) => (
            <Flex key={s} direction="column" alignItems="center" gap={1}>
              <Icon icon="pin" accessibilityLabel="" size={s} />
              <Text size="100" color="subtle">{s}px</Text>
            </Flex>
          ))}
        </Flex>
      </Box>
    </Box>
  );
}

// Renders one plain token group (used inside the taxonomy categories below).
function TokenGroupBlock({ group, query }: { group: TokenGroup | undefined; query: string }) {
  if (!group) return null;
  const items = group.tokens.filter((t) => t.name.toLowerCase().includes(query.toLowerCase()));
  if (items.length === 0) return null;
  const isColor = group.label.toLowerCase().includes("color");
  return (
    <Box marginBottom={6}>
      <Box marginBottom={3}>
        <Heading size="300">{group.label}</Heading>
        <Text size="100" color="subtle">{items.length} tokens</Text>
      </Box>
      <div className={isColor ? "ml-token-grid" : "ml-token-list"}>
        {items.map((t) => (
          <div className="ml-token-item" key={t.name}>
            {isColor && <div className="ml-swatch" style={{ background: `var(${t.name})` }} />}
            <div className="ml-token-meta">
              <code>{t.name}</code>
              <span>{t.value}</span>
            </div>
          </div>
        ))}
      </div>
    </Box>
  );
}

function TokenGroupBlockRaw({
  label,
  items,
  query,
  isColor,
}: {
  label: string;
  items: { name: string; value: string }[];
  query: string;
  isColor?: boolean;
}) {
  const filtered = items.filter((t) => t.name.toLowerCase().includes(query.toLowerCase()));
  if (filtered.length === 0) return null;
  return (
    <Box marginBottom={6}>
      <Box marginBottom={3}>
        <Heading size="300">{label}</Heading>
        <Text size="100" color="subtle">{filtered.length} tokens</Text>
      </Box>
      <div className={isColor ? "ml-token-grid" : "ml-token-list"}>
        {filtered.map((t) => (
          <div className="ml-token-item" key={t.name}>
            {isColor && <div className="ml-swatch" style={{ background: `var(${t.name})` }} />}
            <div className="ml-token-meta">
              <code>{t.name}</code>
              <span>{t.value}</span>
            </div>
          </div>
        ))}
      </div>
    </Box>
  );
}

// Honest callout used wherever a requested taxonomy category doesn't map to
// a literal CSS custom property in Gestalt's shipped output. Says what's
// real instead, and where.
function TokenNote({ tone = "info", children }: { tone?: "info" | "gap"; children: React.ReactNode }) {
  return (
    <Box
      marginBottom={5}
      padding={4}
      rounding={2}
      color={tone === "gap" ? "warningWeak" : "infoWeak"}
    >
      <Text size="200">{children}</Text>
    </Box>
  );
}

const TOKEN_CATEGORIES = [
  { id: "foundation", number: "01", label: "Foundation" },
  { id: "semantic", number: "02", label: "Semantic" },
  { id: "layout", number: "03", label: "Layout" },
  { id: "component", number: "04", label: "Component" },
  { id: "state", number: "05", label: "State" },
  { id: "interaction", number: "06", label: "Interaction" },
  { id: "responsive", number: "07", label: "Responsive" },
  { id: "platform", number: "08", label: "Platform" },
] as const;
type TokenCategoryId = (typeof TOKEN_CATEGORIES)[number]["id"];

function TokenSwatches({ groups, query }: { groups: TokenGroup[]; query: string }) {
  const [category, setCategory] = useState<TokenCategoryId>("foundation");
  const { raw: rawColor, semantic: semanticColor } = useMemo(
    () => splitColorGroup(groups.find((g) => g.label === "Color")),
    [groups]
  );
  const spacing = groups.find((g) => g.label === "Spacing");
  const rounding = groups.find((g) => g.label === "Rounding");
  const fontSize = groups.find((g) => g.label === "Font Size");
  const fontWeight = groups.find((g) => g.label === "Font Weight");
  const elevation = groups.find((g) => g.label === "Elevation");
  const opacity = groups.find((g) => g.label === "Opacity");

  return (
    <Box>
      <div className="ml-token-cat-nav">
        {TOKEN_CATEGORIES.map((c) => (
          <button
            key={c.id}
            className={`ml-token-cat-btn ${category === c.id ? "active" : ""}`}
            onClick={() => setCategory(c.id)}
          >
            <span className="ml-token-cat-num">{c.number}</span> {c.label}
          </button>
        ))}
      </div>

      {category === "foundation" && (
        <Box>
          <TokenNote>
            Raw building blocks: color, typography, spacing, sizing, radius, border, opacity,
            elevation, and motion primitives — everything with a literal, unstyled value before
            any purpose is attached to it.
          </TokenNote>
          <TokenGroupBlockRaw label="Color — raw palette" items={rawColor} query={query} isColor />
          {fontSize && <TokenGroupBlock group={fontSize} query={query} />}
          {fontWeight && <TokenGroupBlock group={fontWeight} query={query} />}
          {spacing && <TokenGroupBlock group={spacing} query={query} />}
          {rounding && <TokenGroupBlock group={rounding} query={query} />}
          <TokenGroupBlockRaw
            label="Border — observed widths"
            items={BORDER.observedWidths.map((v) => ({ name: v, value: `${BORDER.colorRoleCount} color roles use this scale of widths` }))}
            query={query}
          />
          <TokenNote tone="gap">
            Border: {BORDER_GAP} Source: {BORDER_SOURCE}.
          </TokenNote>
          {opacity && <TokenGroupBlock group={opacity} query={query} />}
          {elevation && <TokenGroupBlock group={elevation} query={query} />}
          <TokenGroupBlockRaw
            label="Motion primitives — referenced names"
            items={MOTION_NAMES.map((n) => ({ name: n, value: "unresolved in classic theme" }))}
            query={query}
          />
          <TokenNote tone="gap">
            Motion: {MOTION_GAP} Source: {MOTION_SOURCE}.
          </TokenNote>
        </Box>
      )}

      {category === "semantic" && (
        <Box>
          <TokenNote>
            Purpose-named tokens — the layer components actually consume. Gestalt's real semantic
            color roles (background / text / border / icon / shadow) are distinguished from the
            raw palette above by name, not by a separate file — split out here from the same{" "}
            <code>--color-*</code> set.
          </TokenNote>
          <TokenGroupBlockRaw label="Color — semantic roles" items={semanticColor} query={query} isColor />
        </Box>
      )}

      {category === "layout" && (
        <Box>
          <TokenNote>
            Container, grid, columns, gutters, padding, positioning. Gestalt expresses this as
            components/props, not standalone CSS custom properties — real values below, sourced
            from the actual component API.
          </TokenNote>
          <TokenGroupBlockRaw
            label="Breakpoints"
            items={BREAKPOINTS.map((b) => ({ name: b.name, value: b.value }))}
            query={query}
          />
          <Text size="100" color="subtle">Source: {BREAKPOINTS_SOURCE}</Text>
          <Box marginTop={6}>
            <TokenGroupBlockRaw
              label="Grid — Column"
              items={[
                { name: "columns", value: String(GRID.columns) },
                { name: "responsive props", value: GRID.responsiveProps.join(", ") },
              ]}
              query={query}
            />
            <Text size="100" color="subtle">{GRID.description}</Text>
            <Text size="100" color="subtle">Source: {GRID_SOURCE}</Text>
          </Box>
          <Box marginTop={6}>{spacing && <TokenGroupBlock group={spacing} query={query} />}</Box>
          <TokenNote>
            Gutters and padding reuse the Spacing scale above (Flex/Box <code>gap</code> and{" "}
            <code>padding</code> props) — Gestalt has no separate gutter-specific token.
          </TokenNote>
        </Box>
      )}

      {category === "component" && (
        <TokenNote>
          Component anatomy (button, input, card, modal, navigation, tabs, list, avatar, etc.)
          isn't expressed as standalone design tokens in Gestalt — it's the components themselves.
          See the <strong>Components</strong> tab (57 real, interactive cards) and{" "}
          <strong>Blocks</strong> tab (8 composed patterns) for real anatomy, not a token list here.
        </TokenNote>
      )}

      {category === "state" && (
        <TokenNote>
          Default, hover, focus, pressed, selected, disabled, error, etc. Gestalt encodes these as
          component props and CSS pseudo-classes/data-attributes (e.g. Checkbox's{" "}
          <code>checked</code>/<code>disabled</code>/<code>errorMessage</code>, Button's{" "}
          <code>disabled</code>), not as a standalone state-token file. The semantic color roles
          above already carry state in their names where Gestalt does tokenize it — e.g.{" "}
          <code>--color-border-checkbox-checked-error</code>,{" "}
          <code>--color-background-switch-hover-selected</code>. Try the real states on the
          Checkbox, Switch, and Button cards in the Components tab.
        </TokenNote>
      )}

      {category === "interaction" && (
        <TokenNote>
          Hit area, touch target, gesture, scroll, dismissal. Gestalt enforces these through
          component sizing and behavior (e.g. IconButton's minimum tap target, TapArea's ripple
          and keyboard handling) rather than exposing standalone interaction tokens. No literal
          CSS custom properties exist for this category — see TapArea, IconButton, and Pulsar in
          the Components tab for the real implementations.
        </TokenNote>
      )}

      {category === "responsive" && (
        <Box>
          <TokenNote>
            Breakpoints, adaptive sizing, responsive component transformations — the same real
            breakpoint and grid data as Layout, viewed from the responsive-behavior angle.
          </TokenNote>
          <TokenGroupBlockRaw
            label="Breakpoints"
            items={BREAKPOINTS.map((b) => ({ name: b.name, value: b.value }))}
            query={query}
          />
          <TokenNote>
            Column's <code>smSpan</code>/<code>mdSpan</code>/<code>lgSpan</code> props (Layout
            category) are Gestalt's actual responsive mechanism — span changes per breakpoint
            instead of a separate "responsive spacing" token set. There's no distinct density
            token either; Gestalt doesn't ship a compact/comfortable density variant.
          </TokenNote>
        </Box>
      )}

      {category === "platform" && (
        <Box>
          <TokenGroupBlockRaw
            label="Z-Index — pattern"
            items={[{ name: "FixedZIndex / CompositeZIndex", value: Z_INDEX.pattern }]}
            query={query}
          />
          <Text size="100" color="subtle">{Z_INDEX.description}</Text>
          <Text size="100" color="subtle">Source: {Z_INDEX_SOURCE}</Text>
          <TokenNote tone="gap">{Z_INDEX_GAP}</TokenNote>
          <TokenNote tone="gap">
            App vs Web platform tokens (touch vs pointer, safe area, system UI, native navigation)
            genuinely don't exist in Gestalt — it's a web-only React component library with no
            native-app token layer. MusicLinkd's actual native app is a separate codebase outside
            this Gestalt installation; if it needs platform tokens they'd be defined there, not
            here. Not fabricating an App/Web split that Gestalt itself doesn't have.
          </TokenNote>
        </Box>
      )}
    </Box>
  );
}

function IconGrid({ query }: { query: string }) {
  const filtered = iconNames.filter((n) => n.includes(query.toLowerCase()));
  return (
    <div className="ml-icon-grid">
      {filtered.map((name) => (
        <div className="ml-icon-card" key={name} title={name}>
          <img src={`icons/${name}.svg`} alt={name} loading="lazy" />
          <span>{name}</span>
        </div>
      ))}
      {filtered.length === 0 && <Text color="subtle">No icons match “{query}”.</Text>}
    </div>
  );
}

// ---------------------------------------------------------------------------
// Brand Kit — real MusicLinkd brand assets (colors, logos), the same
// content as the standalone frontend/guide/brandkit/index.html, brought
// into this app as a tab. Deliberately excludes typography: this app's own
// chrome uses Gestalt's real system-font stack (see styles.css), not
// MusicLinkd's brand typefaces (Inter Tight/Inter) — showing both side by
// side inside the same app would visually contradict which font this app
// actually uses. Colors and logos don't have that problem, so those stay.
// ---------------------------------------------------------------------------

const LOGO_SET_FILES = [
  { file: "logo-badge-inverse.svg", fills: "white" },
  { file: "logo-dark-pill.svg", fills: "#FDFDFD, white" },
  { file: "logo-lockup-black.svg", fills: "white, #24211C" },
  { file: "logo-lockup-grey.svg", fills: "white" },
  { file: "logo-lockup-wide-caps.svg", fills: "white" },
  { file: "logo-watermark-stacked.svg", fills: "#F8F9F4, #24211C" },
];

function BrandKitView() {
  const [markColor, setMarkColor] = useState("#0D0B26");
  const faviconInline = faviconMarkSvgRaw
    .replace(/<\?xml[^>]*\?>/, "")
    .replace(/fill="(white|#[0-9a-fA-F]{6})"/g, 'fill="currentColor"')
    .replace("<svg ", '<svg style="width:64px;height:auto" ');
  const markGreyInline = markGreySvgRaw
    .replace(/<\?xml[^>]*\?>/, "")
    .replace(/fill="(white|#[0-9a-fA-F]{6})"/g, 'fill="currentColor"')
    .replace("<svg ", '<svg style="width:64px;height:auto" ');

  return (
    <Box>
      <Box marginBottom={6} padding={4} rounding={2} color="infoWeak">
        <Text size="200">
          Real MusicLinkd brand assets from <code>frontend/guide/brand/README.md</code> and{" "}
          <code>frontend/guide/brand/assets/</code> — same source as the standalone{" "}
          <a href="../brandkit/index.html">brandkit/index.html</a>. Typography is intentionally left out
          here: this app's own chrome uses Gestalt's real system-font stack, not MusicLinkd's brand
          typefaces, so showing both together in the same view would misrepresent which font this app
          actually renders in. See the standalone Brand Kit page for type specimens.
        </Text>
      </Box>

      <Box marginBottom={8}>
        <Box marginBottom={3}><Heading size="300">Color</Heading></Box>
        <Flex gap={3} wrap>
          <Box borderStyle="sm" rounding={3} overflow="hidden" width={200}>
            <Box height={90} dangerouslySetInlineStyle={{ __style: { background: "#EA4C89" } }} />
            <Box padding={3}><Text weight="bold" size="100">Primary</Text><Text size="100" color="subtle">#EA4C89</Text></Box>
          </Box>
          <Box borderStyle="sm" rounding={3} overflow="hidden" width={200}>
            <Box height={90} dangerouslySetInlineStyle={{ __style: { background: "#0D0B26" } }} />
            <Box padding={3}><Text weight="bold" size="100">Primary dark</Text><Text size="100" color="subtle">#0D0B26</Text></Box>
          </Box>
        </Flex>
        <Box marginTop={2}><Text size="100" color="subtle">Only two colors are documented in brand/README.md — no wider palette is invented here.</Text></Box>
      </Box>

      <Box marginBottom={8}>
        <Box marginBottom={3}>
          <Heading size="300">Logo set</Heading>
          <Text size="100" color="subtle">6 files — multi-path lockups. Fill colors mix in ways that don't resolve to one "correct" background from static analysis, so each is shown on both.</Text>
        </Box>
        <div className="ml-comp-grid">
          {LOGO_SET_FILES.map((l) => (
            <Box key={l.file} borderStyle="sm" rounding={3} overflow="hidden">
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr" }}>
                <Box height={90} padding={3} display="flex" alignItems="center" justifyContent="center" dangerouslySetInlineStyle={{ __style: { background: "#FAFAFA" } }}>
                  <img src={`../brand/assets/${l.file}`} alt={`${l.file} on light`} style={{ maxWidth: "100%", maxHeight: "100%" }} />
                </Box>
                <Box height={90} padding={3} display="flex" alignItems="center" justifyContent="center" dangerouslySetInlineStyle={{ __style: { background: "#0D0B26" } }}>
                  <img src={`../brand/assets/${l.file}`} alt={`${l.file} on dark`} style={{ maxWidth: "100%", maxHeight: "100%" }} />
                </Box>
              </div>
              <Box padding={2}>
                <Text size="100"><code>{l.file}</code></Text>
                <Text size="100" color="subtle">Fills: {l.fills}</Text>
              </Box>
            </Box>
          ))}
        </div>
      </Box>

      <Box>
        <Box marginBottom={3}>
          <Heading size="300">App icon set — recolor live</Heading>
          <Text size="100" color="subtle">2 files — single path, single fill each, so unlike the Logo set there's no ambiguity. Inlined with the real fill swapped for currentColor.</Text>
        </Box>
        <Flex gap={2} alignItems="center">
          <label htmlFor="ml-brandkit-color" style={{ fontSize: 12, color: "var(--color-text-subtle, #767676)" }}>Mark color</label>
          <input
            id="ml-brandkit-color"
            type="color"
            value={markColor}
            onChange={(e) => setMarkColor(e.target.value)}
            style={{ width: 36, height: 28, border: "1px solid var(--color-border-container, #e2e2e2)", borderRadius: 6, padding: 2, cursor: "pointer" }}
          />
        </Flex>
        <Box marginTop={3}>
          <Flex gap={3} wrap>
            <Box borderStyle="sm" rounding={3} padding={4} display="flex" alignItems="center" justifyContent="center" dangerouslySetInlineStyle={{ __style: { background: "#0D0B26", color: markColor } }}>
              <div dangerouslySetInnerHTML={{ __html: faviconInline }} />
            </Box>
            <Box borderStyle="sm" rounding={3} padding={4} display="flex" alignItems="center" justifyContent="center" dangerouslySetInlineStyle={{ __style: { background: "#FAFAFA", color: markColor } }}>
              <div dangerouslySetInnerHTML={{ __html: markGreyInline }} />
            </Box>
          </Flex>
        </Box>
      </Box>
    </Box>
  );
}

const TABS = ["components", "blocks", "foundations", "tokens", "icons", "brandkit"] as const;
type Tab = (typeof TABS)[number];

export default function App() {
  const [tab, setTab] = useState<Tab>("components");
  const [query, setQuery] = useState("");
  const [darkMode, setDarkMode] = useState(false);

  const tokenGroups = useMemo(
    () => parseTokenCss(darkMode ? darkTokensCss : lightTokensCss),
    [darkMode]
  );
  // Always the light set — used for theme-invariant scales (spacing,
  // rounding, font-size) in the Foundations tab, since Gestalt's dark CSS
  // only redefines colors and a couple of elevation shadows.
  const baseTokenGroups = useMemo(() => parseTokenCss(lightTokensCss), []);

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", darkMode ? "dark" : "light");
  }, [darkMode]);

  const tabLabel = (t: Tab) => {
    if (t === "components") return `Components (${DEMOS.length})`;
    if (t === "blocks") return `Blocks (${BLOCKS.length})`;
    if (t === "foundations") return "Foundations";
    if (t === "tokens") return "Design Tokens";
    if (t === "icons") return `Icons (${iconNames.length})`;
    return "Brand Kit";
  };

  return (
    <ColorSchemeProvider colorScheme={darkMode ? "dark" : "light"}>
      <DefaultLabelProvider>
        <div className="ml-app">
          <header className="ml-header">
            <div className="ml-header-title">
              <Heading size="600">MusicLinkd System</Heading>
              <Text color="subtle">A live, dynamic UI library — real Gestalt components, real design tokens, real icons, and real composed blocks.</Text>
            </div>
            <Flex gap={3} alignItems="center">
              <Text size="100">{darkMode ? "Dark" : "Light"}</Text>
              <Switch id="theme-switch" switched={darkMode} onChange={({ value }) => setDarkMode(value)} />
            </Flex>
          </header>

          <nav className="ml-nav">
            {TABS.map((t) => (
              <button key={t} className={`ml-tab-btn ${tab === t ? "active" : ""}`} onClick={() => setTab(t)}>
                {tabLabel(t)}
              </button>
            ))}
            <div className="ml-search">
              <SearchField id="global-search" accessibilityLabel="Filter" placeholder={`Filter ${tab}…`} value={query} onChange={({ value }) => setQuery(value)} />
            </div>
          </nav>

          <main className="ml-main">
            {tab === "components" && <ComponentGallery query={query} />}
            {tab === "blocks" && <BlocksGallery query={query} />}
            {tab === "foundations" && <FoundationsView tokenGroups={tokenGroups} baseTokenGroups={baseTokenGroups} />}
            {tab === "tokens" && <TokenSwatches groups={tokenGroups} query={query} />}
            {tab === "icons" && <IconGrid query={query} />}
            {tab === "brandkit" && <BrandKitView />}
          </main>

          <footer className="ml-footer">
            <Text size="100" color="subtle">
              Source: pinterest/gestalt v177.0.13, vendored at <code>frontend/guide/gestalt/</code>. See{" "}
              <a href="../gestalt-library.md">gestalt-library.md</a> and <a href="../INDEX.md">INDEX.md</a> for the full manifest.
            </Text>
          </footer>
        </div>
      </DefaultLabelProvider>
    </ColorSchemeProvider>
  );
}

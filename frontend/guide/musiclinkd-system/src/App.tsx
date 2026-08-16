import React, { useEffect, useMemo, useState } from "react";
import {
  Accordion,
  ActivationCard,
  Avatar,
  AvatarGroup,
  AvatarGroupCluster,
  Badge,
  BannerCallout,
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
  Spinner,
  Status,
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
import { parseTokenCss, type TokenGroup } from "./tokens";
// Inlined at build time (esbuild text loader) instead of fetched at runtime,
// so this app works as a plain double-clicked file:// page — no local
// server required.
import lightTokensCss from "../variables.css";
import darkTokensCss from "../variables-dark.css";

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

function LetterboxDemo() {
  return (
    <Box width={220}>
      <Letterbox contentAspectRatio={1} height={110} width={220}>
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

  { name: "Image", category: "Media & Gallery", description: "Image with placeholder color.", render: () => <ImageDemo /> },
  { name: "Mask", category: "Media & Gallery", description: "Rounds/crops any child content.", render: () => <MaskDemo /> },
  { name: "Letterbox", category: "Media & Gallery", description: "Fits content to a fixed aspect ratio.", render: () => <LetterboxDemo /> },
];

const CATEGORIES: Category[] = ["Actions", "Forms", "Feedback", "Data Display", "Overlays", "Typography & Layout", "Media & Gallery"];

const EXCLUDED_NOTE =
  "Not demoed here, on purpose: Video (needs a real media file), Collage/MasonryV2 (Masonry — the real component, not a stand-in — is demoed in the Blocks tab's Cover art gallery instead), SearchGuide/SearchGuideLink (search-index-specific), ScrollBoundaryContainer/ScrollFetch/TableOfContents (no standalone visual output), WashAnimated/TileData/TagData (Masonry-overlay-specific), Container (a plain unstyled wrapper with nothing to show on its own — not imported here at all). Providers and hooks (ColorSchemeProvider, useColorScheme, etc.) power this app itself rather than being shown as cards.";

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

type Block = { name: string; description: string; render: () => React.ReactNode };

const BLOCKS: Block[] = [
  { name: "Navigation header", description: "Brand + real SearchField + notifications + avatar.", render: () => <NavHeaderBlock /> },
  { name: "Artist profile card", description: "Avatar, tags, and a real toggling Follow button.", render: () => <ArtistProfileBlock /> },
  { name: "Track row", description: "Play/pause toggles the icon and status live.", render: () => <TrackRowBlock /> },
  { name: "Upload release form", description: "TextField + SelectList + Checkbox, button disables until valid.", render: () => <UploadFormBlock /> },
  { name: "Stats row", description: "Three Datapoints with real trend arrows.", render: () => <StatsRowBlock /> },
  { name: "Empty state", description: "Icon + heading + CTA for a catalog with nothing in it yet.", render: () => <EmptyStateBlock /> },
  { name: "Notification stack", description: "Three real BannerSlim variants stacked.", render: () => <NotificationStackBlock /> },
  { name: "Cover art gallery", description: "Real Masonry layout (Pinterest's own algorithm) packing variable-height tiles.", render: () => <GalleryBlock /> },
];

function BlocksGallery({ query }: { query: string }) {
  const filtered = BLOCKS.filter((b) => b.name.toLowerCase().includes(query.toLowerCase()));
  return (
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

function TokenSwatches({ groups, query }: { groups: TokenGroup[]; query: string }) {
  return (
    <Box>
      {groups.map((g) => {
        const items = g.tokens.filter((t) => t.name.toLowerCase().includes(query.toLowerCase()));
        if (items.length === 0) return null;
        const isColor = g.label.toLowerCase().includes("color");
        return (
          <Box key={g.label} marginBottom={8}>
            <Box marginBottom={3}>
              <Heading size="300">{g.label}</Heading>
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
      })}
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

const TABS = ["components", "blocks", "foundations", "tokens", "icons"] as const;
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
    return `Icons (${iconNames.length})`;
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

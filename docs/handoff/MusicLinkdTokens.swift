//
//  MusicLinkdTokens.swift — GENERATED. Do not edit.
//
//  Source:     frontend/guide/tokens/tokens.json  (v9.0.0)
//  Regenerate: node scripts/build-handoff.mjs
//
//  The web preview at /guide and MusicLinkdTokens.kt are generated from this
//  same file, so all three cannot disagree. If a value here looks wrong,
//  change tokens.json and regenerate — do not patch this file.
//
//  Deliberately NOT emitted, for the same reasons as the Compose file:
//    - zIndex            SwiftUI uses view order and .zIndex(), not a scale
//    - layout.max        a web page max-width; on iOS that is a size class
//    - chrome.scrollbar  browser chrome
//    - the brand* colours expression only, never rendered by the app
//
//  Insets are NOT tokens here — read safeAreaInsets from the framework.
//

import SwiftUI

public enum MLColor {
    /// Primary action AND primary text
    public static let ink = Color(red: 0.0510, green: 0.0510, blue: 0.0510)
    /// App canvas / page background
    public static let paper = Color(red: 0.9882, green: 0.9882, blue: 0.9922)
    /// Card and sheet fill
    public static let white = Color(red: 1.0000, green: 1.0000, blue: 1.0000)
    /// Subtle raised/sunken fill — search bars, inactive segments, skeletons
    public static let surface = Color(red: 0.9725, green: 0.9725, blue: 0.9843)
    /// Hairline borders, dividers, list separators
    public static let line = Color(red: 0.9255, green: 0.9255, blue: 0.9451)
    /// Secondary text, metadata, placeholder
    public static let muted = Color(red: 0.4314, green: 0.4314, blue: 0.4902)
    /// Brand accent — focus ring, selection, active tab, brand mark, waveform progress
    public static let pink = Color(red: 0.9176, green: 0.2980, blue: 0.5373)
    /// Two jobs, one token
    public static let pinkPressed = Color(red: 0.7804, green: 0.1608, blue: 0.3725)
    /// The one permitted tinted surface — accent chips, progress tracks
    public static let pinkContainer = Color(red: 0.9882, green: 0.9059, blue: 0.9529)
    /// Pressed state of a primary (black) control — lifts off pure black so the press is visible
    public static let inkPressed = Color(red: 0.1686, green: 0.1686, blue: 0.1686)
    /// 
    public static let success = Color(red: 0.1333, green: 0.7725, blue: 0.3686)
    /// 
    public static let warning = Color(red: 0.9608, green: 0.6196, blue: 0.0431)
    /// 
    public static let error = Color(red: 0.8627, green: 0.1490, blue: 0.1490)
    /// Informational badge — 'On Tour', 'Remote available'
    public static let info = Color(red: 0.1451, green: 0.3882, blue: 0.9216)
    /// Inactive control borders and text
    public static let disabled = Color(red: 0.6118, green: 0.6392, blue: 0.6863)
    /// Backdrop behind drawers, sheets and modals
    public static let scrim = Color(red: 0.0510, green: 0.0510, blue: 0.0510).opacity(0.32)
    /// BADGES ONLY
    public static let purple = Color(red: 0.5451, green: 0.3608, blue: 0.9647)
}

public enum MLSpace {
    /// Icon gaps, badge internals
    public static let s1: CGFloat = 4
    /// Compact gaps, chip padding
    public static let s2: CGFloat = 8
    /// Card padding, default gap, screen gutter
    public static let s3: CGFloat = 16
    /// Section spacing, hero padding
    public static let s4: CGFloat = 32
}

/// Radius. Step 4 is Capsule(), not a number — a 9999 corner radius is a web
/// idiom; SwiftUI has a shape for it and clipping to it is exact at any size.
public enum MLRadius {
    /// Chips, small controls, inline media thumbs
    public static let r1: CGFloat = 8
    /// Media, inputs, toasts, sheets
    public static let r2: CGFloat = 16
    /// Cards, large containers
    public static let r3: CGFloat = 32
    public static let capsule = Capsule()
}

/// Control heights. Step 3 is **44**, not Android's 48 — that is Apple's
/// touch-target minimum, and using Material's number here would be wrong on
/// both platforms at once.
public enum MLSize {
    /// Chip, small button, compact icon button
    public static let s1: CGFloat = 32
    /// Default input and secondary button
    public static let s2: CGFloat = 40
    /// MINIMUM TOUCH TARGET
    public static let s3: CGFloat = 44
    /// Comfortable target — primary CTAs, FAB, player controls
    public static let s4: CGFloat = 56
}

public enum MLIcon {
    /// Inline with --type-1 caption text; also the native size of the 25 Gestalt 'compact' glyphs
    public static let i1: CGFloat = 16
    /// Default UI icon — buttons, fields, list rows, menu items
    public static let i2: CGFloat = 20
    /// Navigation and primary actions; the native grid of the other 398 glyphs
    public static let i3: CGFloat = 24
    /// Large — empty states, feature discs, illustration-scale marks
    public static let i4: CGFloat = 32
}

public enum MLAvatar {
    /// Facepile / stacked collaborators, inline byline
    public static let a1: CGFloat = 24
    /// List rows, comment threads, chat list
    public static let a2: CGFloat = 32
    /// Card headers, message bubbles, seller row
    public static let a3: CGFloat = 48
    /// Profile header, booking confirmation, hero
    public static let a4: CGFloat = 64
}

public enum MLBorder {
    /// Subtle — hairline card borders, dividers, list separators, input rest
    public static let b1: CGFloat = 1
    /// Prominent — focus ring, selected card or chip, featured tier
    public static let b2: CGFloat = 2
}

public enum MLOpacity {
    /// Hover overlay (web) / ripple start (native)
    public static let o1: Double = 0.08
    /// Pressed feedback
    public static let o2: Double = 0.16
    /// Disabled content
    public static let o3: Double = 0.32
    /// Modal scrim / media gradient
    public static let o4: Double = 0.64
}

/// Font weight — 400/500/600/800 as Apple's named weights. There is
/// deliberately no .bold step: the heavy UI weight was cut from 700 to 600 on
/// 2026-08-31 and leaving one would let it creep back.
public enum MLWeight {
    /// Body copy, running text
    public static let w1: Font.Weight = .regular
    /// Medium — list titles, secondary labels
    public static let w2: Font.Weight = .medium
    /// Semibold — the system's emphasis weight
    public static let w3: Font.Weight = .semibold
    /// Display only — hero headlines and the one big number on a screen
    public static let w4: Font.Weight = .heavy
}

/// Type. Built on Dynamic Type text styles so every step scales with the
/// user's accessibility setting — the same reason Android authors an sp ramp
/// rather than reusing the web clamp().
public enum MLType {
    /// Micro labels, captions, badges, metadata, timestamps
    public static let type1 = Font.system(.caption1, design: .default).weight(.semibold)
    /// Body copy, default UI text, list item titles
    public static let type2 = Font.system(.body, design: .default).weight(.regular)
    /// Section headings, card titles, screen titles
    public static let type3 = Font.system(.title3, design: .default).weight(.semibold)
    /// Display — hero headlines, now-playing track title, the one big number on a screen
    public static let type4 = Font.system(.largeTitle, design: .default).weight(.heavy)
}

/// Motion — response/dampingFraction, which is how SwiftUI expresses a spring.
/// Not a duration, and not Compose's dampingRatio/stiffness pair.
public enum MLMotion {
    /// Press, ripple, toggle, chip select — must feel instant
    public static let spring1 = Animation.spring(response: 0.15, dampingFraction: 1)
    /// Icon change, colour change, hover lift, dismissal
    public static let spring2 = Animation.spring(response: 0.25, dampingFraction: 0.9)
    /// Sheet reveal, card lift, shared-element / container transform
    public static let spring3 = Animation.spring(response: 0.35, dampingFraction: 0.85)
    /// Entrance, modal, splash, expressive moments (the Shazam result reveal)
    public static let spring4 = Animation.spring(response: 0.5, dampingFraction: 0.75)
}

public struct Shadow {
    public let radius: CGFloat
    public let y: CGFloat
    public let opacity: Double
}

/// Elevation. Prefer a hairline border to a shadow; these exist for the cases
/// that genuinely lift off the surface.
public enum MLElevation {
    /// Cards, chips, resting surfaces
    public static let e1 = Shadow(radius: 3, y: 1, opacity: 0.08)
    /// Hover cards, dropdowns, popovers
    public static let e2 = Shadow(radius: 12, y: 4, opacity: 0.05)
    /// FAB, side sheet, bottom sheet, mini-player
    public static let e3 = Shadow(radius: 24, y: 8, opacity: 0.08)
    /// Modals, dialogs, full-screen overlays
    public static let e4 = Shadow(radius: 32, y: 12, opacity: 0.12)
}

public enum MLGesture {
    /// Distance past which a swipe commits — sheet dismiss, swipe deck, row actions
    public static let swipeThreshold: CGFloat = 80
    /// Movement before a drag is recognised over a tap
    public static let dragThreshold: CGFloat = 8
    /// Long-press recognition delay
    public static let longPress: TimeInterval = 0.5
    /// Minimum pinch-zoom scale — artwork and waveform detail views
    public static let pinchMin: CGFloat = 0.5
    /// Maximum pinch-zoom scale
    public static let pinchMax: CGFloat = 3
}

/// Dimensions fixed by a platform contract rather than chosen by us.
public enum MLComponent {
    /// Search app bar — the spec's 56dp variant
    public static let appbarSearch: CGFloat = 56
    /// Center-aligned app bar, generic screens
    public static let appbarCenter: CGFloat = 64
    /// Medium app bar, section headers
    public static let appbarMedium: CGFloat = 112
    /// Large app bar, profile pages and dashboard
    public static let appbarLarge: CGFloat = 152
    /// Modal navigation drawer — mobile hamburger menu
    public static let drawerModal: CGFloat = 360
    /// Persistent drawer — desktop sidebar
    public static let drawerPersistent: CGFloat = 240
    /// Navigation rail, tablet only, 3-7 destinations
    public static let navRail: CGFloat = 80
    /// Material 3 switch track width
    public static let switchTrack: CGFloat = 52
}

/// Haptics are UIKit generators, not values:
//   selection  UISelectionFeedbackGenerator
//   impact     UIImpactFeedbackGenerator(.light)
//   success    UINotificationFeedbackGenerator(.success)
//   error      UINotificationFeedbackGenerator(.error)

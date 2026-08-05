# Styleguide — Accessibility Implementation Checklist

## Accessibility Verification Matrix

- [x] **Touch Targets:** All clickable icons (`IconButton`), chips, and buttons maintain minimum `48.dp x 48.dp` bounds.
- [x] **Contrast Ratio:** Body typography (`#0D0B26`) on background canvas (`#FCFCFD`) achieves > 7:1 contrast ratio (WCAG AAA).
- [x] **Screen Reader Support:** All non-text visual icons include explicit `contentDescription` strings in `res/values/strings.xml`.
- [x] **Dynamic Font Scaling:** All font sizes use `sp` units allowing OS-level accessibility font scaling without text clipping.
- [x] **Keyboard / Focus Navigation:** Focus indicators highlight active controls when navigating via hardware keyboard.

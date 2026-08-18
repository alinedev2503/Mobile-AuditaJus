---
name: Lex Dark Sovereign
colors:
  surface: '#101415'
  surface-dim: '#101415'
  surface-bright: '#363a3b'
  surface-container-lowest: '#0b0f10'
  surface-container-low: '#191c1e'
  surface-container: '#1d2022'
  surface-container-high: '#272a2c'
  surface-container-highest: '#323537'
  on-surface: '#e0e3e5'
  on-surface-variant: '#c3c6d7'
  inverse-surface: '#e0e3e5'
  inverse-on-surface: '#2d3133'
  outline: '#8d90a0'
  outline-variant: '#434655'
  surface-tint: '#b4c5ff'
  primary: '#b4c5ff'
  on-primary: '#002a78'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#0053db'
  secondary: '#b9c7e0'
  on-secondary: '#233144'
  secondary-container: '#3c4a5e'
  on-secondary-container: '#abb9d2'
  tertiary: '#bec6e0'
  on-tertiary: '#283044'
  tertiary-container: '#656d84'
  on-tertiary-container: '#eef0ff'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#d5e3fd'
  secondary-fixed-dim: '#b9c7e0'
  on-secondary-fixed: '#0d1c2f'
  on-secondary-fixed-variant: '#3a485c'
  tertiary-fixed: '#dae2fd'
  tertiary-fixed-dim: '#bec6e0'
  on-tertiary-fixed: '#131b2e'
  on-tertiary-fixed-variant: '#3f465c'
  background: '#101415'
  on-background: '#e0e3e5'
  surface-variant: '#323537'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.3'
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.4'
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.5'
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1'
    letterSpacing: 0.05em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: '1.3'
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 24px
  lg: 48px
  xl: 80px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style

The design system is a high-performance, dark-mode-first framework tailored for legal and financial professionals who demand focus and precision. The aesthetic is a fusion of **Corporate Modern** and **Minimalism**, stripping away visual noise to prioritize data density and clarity.

The personality is authoritative, secure, and technologically advanced. By utilizing deep navy and charcoal foundations, the UI reduces eye strain during long hours of legal analysis while projecting a sense of sophisticated reliability. The interface uses subtle depth cues and high-contrast accents to guide the user’s eye toward critical actions without overwhelming the visual field.

## Colors

The palette is anchored by a deep navy background (`#0F172A`), providing a low-light foundation that ensures the primary "Trustworthy Blue" (`#2563EB`) appears vibrant and actionable. 

Surface levels are strictly defined to create hierarchy:
- **Base Background**: Deep navy for the main application shell.
- **Surface Layer**: Dark charcoal for primary cards and content containers.
- **Elevated Layer**: Lighter slate for tooltips, modals, and hover states.

Semantic colors (Success, Error, Warning) are tuned to 500-600 weight values to maintain WCAG AA contrast against dark surfaces while avoiding the "neon" vibration often found in lower-quality dark modes.

## Typography

This design system utilizes **Inter** exclusively to leverage its exceptional legibility and systematic rhythm. 

- **Hierarchy**: Use `F8FAFC` (High Contrast White) for all headlines to ensure immediate scannability.
- **Readability**: Body text uses `CBD5E1` (Light Gray) to reduce the harshness of pure white-on-black text, which can cause "halation" for users with astigmatism.
- **Labels**: Small labels and captions should use uppercase with slight letter spacing to maintain distinctiveness from body paragraphs.

## Layout & Spacing

The system follows a strict **8px grid** (Round Eight) to ensure mathematical harmony across all components.

- **Grid Model**: A 12-column fluid grid for desktop with 24px gutters.
- **Desktop**: Content is typically contained within a 1280px max-width wrapper, centered on the deep navy background.
- **Mobile**: Transitions to a 4-column grid with 16px side margins.
- **Density**: Because this is a professional tool, padding is generous around structural blocks (MD/LG) but tighter within data components (XS/SM) to allow for high information density.

## Elevation & Depth

In this dark environment, depth is communicated through **Tonal Layering** rather than heavy shadows. 

1. **Lowest Elevation (Background)**: `#0F172A`.
2. **Mid Elevation (Cards/Sidebar)**: `#1E293B`.
3. **High Elevation (Modals/Popovers)**: `#334155`.

Shadows, when used, are ultra-dark and diffused (`rgba(0,0,0,0.4)`), serving more as an "outer glow" of darkness to separate floating elements from the background. Subtle 1px borders using `#334155` (the elevated surface color) are preferred over shadows for defining component boundaries.

## Shapes

Adhering to the "Round Eight" philosophy, the design system utilizes a consistent **0.5rem (8px)** base corner radius.

- **Standard Components**: Buttons, Input Fields, and Small Cards use the 8px radius.
- **Large Containers**: Main content areas or dashboard widgets use `rounded-lg` (16px).
- **Interactive Elements**: Subtle "pill" shapes are reserved exclusively for status tags (Chips) to differentiate them from actionable buttons.

## Components

### Buttons
- **Primary**: Solid `#2563EB` with white text. Hover state should be a slightly lighter blue.
- **Secondary**: Ghost style with a 1px border of `#334155` and white text.
- **Tertiary**: Text-only, using the Primary Blue for links and utility actions.

### Input Fields
- **Background**: `#1E293B`.
- **Border**: 1px solid `#334155`.
- **Focus State**: Border color shifts to Primary Blue with a 2px outer glow.

### Cards
- Always use `#1E293B` as the surface color. 
- Headers within cards should be separated by a 1px divider of `#334155`.

### Status Chips
- **Neutral**: Background `#334155`, Text `#CBD5E1`.
- **Success/Error**: Use low-opacity versions of the semantic colors for the background (e.g., 15% opacity) with full-saturation text for high legibility.

### Data Tables
- Header row uses a slightly darker shade or `#0F172A` to anchor the content.
- Rows use a subtle `#334155` bottom border for separation. Avoid zebra striping; use hover highlights instead.
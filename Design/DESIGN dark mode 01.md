---
name: Lex Dark Professional
colors:
  surface: '#0b1326'
  surface-dim: '#0b1326'
  surface-bright: '#31394d'
  surface-container-lowest: '#060e20'
  surface-container-low: '#131b2e'
  surface-container: '#171f33'
  surface-container-high: '#222a3d'
  surface-container-highest: '#2d3449'
  on-surface: '#dae2fd'
  on-surface-variant: '#c2c6d6'
  inverse-surface: '#dae2fd'
  inverse-on-surface: '#283044'
  outline: '#8c909f'
  outline-variant: '#424754'
  surface-tint: '#adc6ff'
  primary: '#adc6ff'
  on-primary: '#002e6a'
  primary-container: '#4d8eff'
  on-primary-container: '#00285d'
  inverse-primary: '#005ac2'
  secondary: '#b9c8de'
  on-secondary: '#233143'
  secondary-container: '#39485a'
  on-secondary-container: '#a7b6cc'
  tertiary: '#4edea3'
  on-tertiary: '#003824'
  tertiary-container: '#00a572'
  on-tertiary-container: '#00311f'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#d8e2ff'
  primary-fixed-dim: '#adc6ff'
  on-primary-fixed: '#001a42'
  on-primary-fixed-variant: '#004395'
  secondary-fixed: '#d4e4fa'
  secondary-fixed-dim: '#b9c8de'
  on-secondary-fixed: '#0d1c2d'
  on-secondary-fixed-variant: '#39485a'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#0b1326'
  on-background: '#dae2fd'
  surface-variant: '#2d3449'
typography:
  display-lg:
    fontFamily: Domine
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Domine
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Domine
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-md:
    fontFamily: Domine
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Work Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Work Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Work Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Work Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Work Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 40px
  container-max: 1280px
  gutter: 24px
  margin-mobile: 16px
---

## Brand & Style
This design system is engineered for legal professionals who require a high-focus, low-fatigue environment for long-form reading and data analysis. The brand personality is authoritative, precise, and sophisticated. 

The aesthetic is **Corporate Modern** with a focus on **Tonal Layering**. It utilizes a deep palette to minimize eye strain while maintaining a sense of prestige. The UI avoids unnecessary decorative elements, opting instead for crisp alignment, generous negative space, and subtle depth to indicate hierarchy. The emotional response should be one of security, clarity, and unwavering professional reliability.

## Colors
The palette is centered on a deep navy foundation. The primary blue has been shifted to `#3b82f6` from the original brand color to ensure it meets WCAG AA contrast standards against the charcoal surfaces and provides a subtle luminance without causing "vibration" against the dark background.

- **Primary (#3b82f6):** Used for primary actions, active states, and brand markers.
- **Secondary (#94a3b8):** Reserved for supporting text and non-essential icons.
- **Tertiary (#10b981):** A specialized success green for legal approvals and positive status indicators.
- **Neutral/Background (#0f172a):** The "True Deep" used for the base page background.
- **Surface (#1e293b):** The secondary layer used for cards, sidebars, and input fields to create a distinct visual lift.

## Typography
The typography system uses a hybrid approach to balance tradition with utility. **Domine** is used for headlines to provide a sturdy, authoritative, and "legal" feel that evokes traditional print law journals. **Work Sans** is used for all functional UI and body text due to its exceptional legibility in dark mode and its neutral, professional character.

For legal documents and long-form case files, use `body-lg` to ensure maximum readability. All labels should use `label-md` with uppercase styling to differentiate them from interactive body text.

## Layout & Spacing
The layout follows a strict 12-column grid for desktop environments. To maintain a "Pro" feel, the system uses a 4px baseline grid to ensure all elements align with mathematical precision. 

- **Desktop:** 12 columns, 24px gutters, max-width of 1280px. Content is centered.
- **Tablet:** 8 columns, 16px gutters, flexible width.
- **Mobile:** 4 columns, 16px margins, vertical stack.

Information density is prioritized. Use `md` (16px) spacing for internal card padding and `lg` (24px) for margins between major layout sections.

## Elevation & Depth
In this dark mode environment, depth is communicated through **Tonal Layering** rather than heavy shadows. 

1.  **Level 0 (Base):** `#0f172a` — The canvas layer.
2.  **Level 1 (Surface):** `#1e293b` — Used for cards, navigation bars, and modals.
3.  **Level 2 (Interaction):** `#334155` — Used for hovered states or secondary buttons.

A subtle "Glow Shadow" is reserved exclusively for the primary action button and active modals to draw the eye. This shadow uses the primary color at 20% opacity with a large 24px blur. All containers should have a 1px solid border of `#334155` to ensure crisp separation between different content blocks.

## Shapes
The design system utilizes a **Soft** shape language. This adds a hint of modern approachability without compromising the serious nature of legal software.

- **Buttons & Inputs:** 0.25rem (4px) corner radius.
- **Cards & Modals:** 0.5rem (8px) corner radius (rounded-lg).
- **Data Tables:** Should remain sharp or use the minimum 4px radius on the outer container only.

## Components

### Buttons
- **Primary:** Background `#3b82f6`, text `#ffffff`. On hover, apply a slight brightness increase and the primary glow shadow.
- **Secondary:** Transparent background, 1px border of `#334155`, text `#94a3b8`. 
- **Ghost:** No border or background, text `#3b82f6`. Used for tertiary actions.

### Cards
Cards are the primary container for case information. They must use the `#1e293b` surface color with a 1px border of `#334155`. Header sections within cards should be separated by a subtle horizontal rule.

### Input Fields
Inputs use a darker background than the card surface (`#0f172a`) to create an "inset" feel. The border should brighten to `#3b82f6` only when the field is focused. Placeholder text should use `#475569`.

### Data Tables
Tables are critical for legal accounting. Use a "Zebra Stripe" pattern where alternating rows use a slightly different tint of the surface color. Headers must be `label-md` and pinned during scroll.

### Chips & Status Indicators
Status chips (e.g., "Pending", "Filed", "Overdue") should use low-saturation background tints with high-saturation text to maintain readability without overwhelming the dark interface. For example, a "Pending" status uses a dark amber background with a bright yellow text.
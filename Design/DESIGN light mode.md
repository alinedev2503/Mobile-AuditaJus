---
name: Contador Jurídico Pro
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#434655'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#737686'
  outline-variant: '#c3c6d7'
  surface-tint: '#0053db'
  primary: '#004ac6'
  on-primary: '#ffffff'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#b4c5ff'
  secondary: '#565e74'
  on-secondary: '#ffffff'
  secondary-container: '#dae2fd'
  on-secondary-container: '#5c647a'
  tertiary: '#4e565c'
  on-tertiary: '#ffffff'
  tertiary-container: '#676e75'
  on-tertiary-container: '#eaf1f9'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#dae2fd'
  secondary-fixed-dim: '#bec6e0'
  on-secondary-fixed: '#131b2e'
  on-secondary-fixed-variant: '#3f465c'
  tertiary-fixed: '#dce3eb'
  tertiary-fixed-dim: '#c0c7cf'
  on-tertiary-fixed: '#151c22'
  on-tertiary-fixed-variant: '#40484e'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-bold:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 48px
  container-max-width: 1200px
---

## Brand & Style
The design system is anchored in **Minimalism** with a focus on **Professional Reliability**. It is designed for citizens navigating legal complexities, requiring a UI that feels authoritative yet accessible. The aesthetic prioritizes clarity, utilizing generous whitespace to reduce cognitive load and high-contrast typography to ensure legibility. The emotional response should be one of calm confidence—moving legal auditing from a point of stress to a point of structured control.

## Colors
This design system utilizes a high-trust palette rooted in judicial blues.
- **Primary (#2563EB):** Used for primary call-to-actions, focus states, and active indicators.
- **Secondary (#0F172A):** Reserved for headers, navigation backgrounds, and high-level structural elements to provide a "Deep Navy" anchor.
- **Tertiary (#F0F7FF):** A soft light-blue tint used for secondary containers, background sections, and grouping related information without adding visual weight.
- **Surface:** The core canvas is pure white (#FFFFFF) to maintain a sterile, professional audit environment.
- **Status:** Standard semantic colors apply (Success: #10B981, Error: #EF4444) but should be used sparingly against the neutral backdrop.

## Typography
The system relies exclusively on **Inter** to project a modern, systematic, and utilitarian feel. 
- **Hierarchy:** Use bold weights (700) for headers to establish an immediate information hierarchy. 
- **Contrast:** Maintain high contrast between text and background; body text should default to #1E293B (Slate 800) rather than pure black to improve long-form reading comfort.
- **Scale:** On mobile devices, headlines scale down to prevent awkward wrapping, ensuring that legal document titles remain readable in a single glance.

## Layout & Spacing
The design system employs a **Fixed Grid** on desktop (12 columns) and a **Fluid Grid** on mobile (4 columns). 
- **Rhythm:** A 4px baseline grid governs all spacing.
- **Margins:** Use wide 48px margins on desktop to reinforce the minimalist "gallery" feel for audit data. Mobile uses a tighter 16px margin to maximize screen real estate.
- **Alignment:** Content should be centered within a 1200px max-width container on larger screens to prevent line lengths from becoming unreadable.

## Elevation & Depth
Depth is conveyed through **Ambient Shadows** and **Tonal Layers**.
- **Shadows:** Use a single, consistent soft shadow for elevated elements: `0 4px 6px rgba(0, 0, 0, 0.05)`. This adds a subtle "lift" without appearing heavy or dated.
- **Surface Tiers:** Use the tertiary blue (#F0F7FF) to create nested depth. A white card sitting on a light-blue section background is the preferred method for grouping content.
- **Borders:** Use subtle 1px borders (#E2E8F0) for input fields and static cards to maintain structure in the absence of heavy shadows.

## Shapes
The shape language balances approachability with professional structure.
- **Containers & Cards:** A base radius of **12px** is mandatory for all primary containers, modals, and cards.
- **Interactive Elements:** Buttons and tags deviate from the container radius to use a **Pill-shaped (Full Round)** aesthetic, signaling clear interactivity and a modern tech-oriented friendliness.

## Components
- **Buttons:** Must be pill-shaped. Primary buttons use Trustworthy Blue (#2563EB) with white bold text. Secondary buttons use an outline style or the Deep Navy background.
- **Input Fields:** 12px border radius with a 1px border. Focus states must use a 2px Primary Blue glow. Labels should be positioned above the field in `label-bold` style.
- **Cards:** White background, 12px radius, and the standard soft ambient shadow. Used for individual audit entries or legal case summaries.
- **Chips/Badges:** Pill-shaped with light-blue backgrounds and Primary Blue text for status indicators (e.g., "In Review", "Audited").
- **Lists:** Clean, border-less rows separated by subtle 1px dividers. Each row should have a hover state using the Tertiary Blue tint.
- **Audit Progress Bar:** A custom component using a thick 8px track (Tertiary Blue) and a rounded Primary Blue indicator to show completion of legal checklists.
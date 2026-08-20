package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Design tokens — sourced 1:1 from /Design/DESIGN light mode.md
// ("Contador Jurídico Pro") and /Design/DESIGN darkmode 02.md ("Lex Dark
// Sovereign"). Do not hand-tune these; edit the source .md files and re-sync.
// ============================================================================

// ---- Light theme — "Contador Jurídico Pro" ----
val LightSurface = Color(0xFFF8F9FF)
val LightSurfaceDim = Color(0xFFCBDBF5)
val LightSurfaceBright = Color(0xFFF8F9FF)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFEFF4FF)
val LightSurfaceContainer = Color(0xFFE5EEFF)
val LightSurfaceContainerHigh = Color(0xFFDCE9FF)
val LightSurfaceContainerHighest = Color(0xFFD3E4FE)
val LightOnSurface = Color(0xFF0B1C30)
val LightOnSurfaceVariant = Color(0xFF434655)
val LightInverseSurface = Color(0xFF213145)
val LightInverseOnSurface = Color(0xFFEAF1FF)
val LightOutline = Color(0xFF737686)
val LightOutlineVariant = Color(0xFFC3C6D7)
val LightSurfaceTint = Color(0xFF0053DB)
val LightPrimary = Color(0xFF004AC6)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFF2563EB)
val LightOnPrimaryContainer = Color(0xFFEEEFFF)
val LightInversePrimary = Color(0xFFB4C5FF)
val LightSecondary = Color(0xFF565E74)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFDAE2FD)
val LightOnSecondaryContainer = Color(0xFF5C647A)
val LightTertiary = Color(0xFF4E565C)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFF676E75)
val LightOnTertiaryContainer = Color(0xFFEAF1F9)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF93000A)
val LightBackground = Color(0xFFF8F9FF)
val LightOnBackground = Color(0xFF0B1C30)
val LightSurfaceVariant = Color(0xFFD3E4FE)

// ---- Dark theme — "Lex Dark Sovereign" ----
val DarkSurface = Color(0xFF101415)
val DarkSurfaceDim = Color(0xFF101415)
val DarkSurfaceBright = Color(0xFF363A3B)
val DarkSurfaceContainerLowest = Color(0xFF0B0F10)
val DarkSurfaceContainerLow = Color(0xFF191C1E)
val DarkSurfaceContainer = Color(0xFF1D2022)
val DarkSurfaceContainerHigh = Color(0xFF272A2C)
val DarkSurfaceContainerHighest = Color(0xFF323537)
val DarkOnSurface = Color(0xFFE0E3E5)
val DarkOnSurfaceVariant = Color(0xFFC3C6D7)
val DarkInverseSurface = Color(0xFFE0E3E5)
val DarkInverseOnSurface = Color(0xFF2D3133)
val DarkOutline = Color(0xFF8D90A0)
val DarkOutlineVariant = Color(0xFF434655)
val DarkSurfaceTint = Color(0xFFB4C5FF)
val DarkPrimary = Color(0xFFB4C5FF)
val DarkOnPrimary = Color(0xFF002A78)
val DarkPrimaryContainer = Color(0xFF2563EB)
val DarkOnPrimaryContainer = Color(0xFFEEEFFF)
val DarkInversePrimary = Color(0xFF0053DB)
val DarkSecondary = Color(0xFFB9C7E0)
val DarkOnSecondary = Color(0xFF233144)
val DarkSecondaryContainer = Color(0xFF3C4A5E)
val DarkOnSecondaryContainer = Color(0xFFABB9D2)
val DarkTertiary = Color(0xFFBEC6E0)
val DarkOnTertiary = Color(0xFF283044)
val DarkTertiaryContainer = Color(0xFF656D84)
val DarkOnTertiaryContainer = Color(0xFFEEF0FF)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)
val DarkBackground = Color(0xFF101415)
val DarkOnBackground = Color(0xFFE0E3E5)
val DarkSurfaceVariant = Color(0xFF323537)

// ============================================================================
// Semantic status colors — not defined explicitly in either .md spec, but
// referenced in prose ("Semantic colors tuned to 500-600 weight values...").
// Centralized here so every "Analisando", "Petição Pronta", "Protocolado",
// etc. chip across the app draws from one source instead of ad-hoc hex.
// ============================================================================

// Warning / "em envio, aguardando" — amber family
val WarningLight = Color(0xFFD97706)
val WarningContainerLight = Color(0xFFFEF3C7)
val WarningDark = Color(0xFFF7C577)
val WarningContainerDark = Color(0xFF4A3313)

// Success / "concluído, petição pronta" — green family
val SuccessLight = Color(0xFF16A34A)
val SuccessContainerLight = Color(0xFFDCFCE7)
val SuccessDark = Color(0xFF7FD99A)
val SuccessContainerDark = Color(0xFF163C24)

// Info / "analisando, processando IA" — reuses brand blue
val InfoLight = LightPrimaryContainer
val InfoContainerLight = Color(0xFFDBEAFE)
val InfoDark = DarkPrimary
val InfoContainerDark = Color(0xFF1E2A4A)

// Protocolado / "enviado à corte" — violet accent, purely a status marker
val FiledLight = Color(0xFF7C3AED)
val FiledContainerLight = Color(0xFFEDE9FE)
val FiledDark = Color(0xFFCBB6FF)
val FiledContainerDark = Color(0xFF352359)

// Neutral / pending — matches on-surface-variant family
val NeutralLight = LightOnSurfaceVariant
val NeutralContainerLight = LightSurfaceContainer
val NeutralDark = DarkOnSurfaceVariant
val NeutralContainerDark = DarkSurfaceContainer

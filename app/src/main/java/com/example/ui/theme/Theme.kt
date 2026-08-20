package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// ============================================================================
// Colors — mapped 1:1 from the `colors:` front-matter of each spec.
// ============================================================================

private val LightColorScheme = lightColorScheme(
  primary = LightPrimary,
  onPrimary = LightOnPrimary,
  primaryContainer = LightPrimaryContainer,
  onPrimaryContainer = LightOnPrimaryContainer,
  inversePrimary = LightInversePrimary,
  secondary = LightSecondary,
  onSecondary = LightOnSecondary,
  secondaryContainer = LightSecondaryContainer,
  onSecondaryContainer = LightOnSecondaryContainer,
  tertiary = LightTertiary,
  onTertiary = LightOnTertiary,
  tertiaryContainer = LightTertiaryContainer,
  onTertiaryContainer = LightOnTertiaryContainer,
  background = LightBackground,
  onBackground = LightOnBackground,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightOnSurfaceVariant,
  surfaceTint = LightSurfaceTint,
  surfaceContainerLowest = LightSurfaceContainerLowest,
  surfaceContainerLow = LightSurfaceContainerLow,
  surfaceContainer = LightSurfaceContainer,
  surfaceContainerHigh = LightSurfaceContainerHigh,
  surfaceContainerHighest = LightSurfaceContainerHighest,
  surfaceDim = LightSurfaceDim,
  surfaceBright = LightSurfaceBright,
  inverseSurface = LightInverseSurface,
  inverseOnSurface = LightInverseOnSurface,
  outline = LightOutline,
  outlineVariant = LightOutlineVariant,
  error = LightError,
  onError = LightOnError,
  errorContainer = LightErrorContainer,
  onErrorContainer = LightOnErrorContainer,
)

private val DarkColorScheme = darkColorScheme(
  primary = DarkPrimary,
  onPrimary = DarkOnPrimary,
  primaryContainer = DarkPrimaryContainer,
  onPrimaryContainer = DarkOnPrimaryContainer,
  inversePrimary = DarkInversePrimary,
  secondary = DarkSecondary,
  onSecondary = DarkOnSecondary,
  secondaryContainer = DarkSecondaryContainer,
  onSecondaryContainer = DarkOnSecondaryContainer,
  tertiary = DarkTertiary,
  onTertiary = DarkOnTertiary,
  tertiaryContainer = DarkTertiaryContainer,
  onTertiaryContainer = DarkOnTertiaryContainer,
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkOnSurfaceVariant,
  surfaceTint = DarkSurfaceTint,
  surfaceContainerLowest = DarkSurfaceContainerLowest,
  surfaceContainerLow = DarkSurfaceContainerLow,
  surfaceContainer = DarkSurfaceContainer,
  surfaceContainerHigh = DarkSurfaceContainerHigh,
  surfaceContainerHighest = DarkSurfaceContainerHighest,
  surfaceDim = DarkSurfaceDim,
  surfaceBright = DarkSurfaceBright,
  inverseSurface = DarkInverseSurface,
  inverseOnSurface = DarkInverseOnSurface,
  outline = DarkOutline,
  outlineVariant = DarkOutlineVariant,
  error = DarkError,
  onError = DarkOnError,
  errorContainer = DarkErrorContainer,
  onErrorContainer = DarkOnErrorContainer,
)

// ============================================================================
// Shapes — mapped from the `rounded:` front-matter, which is byte-for-byte
// identical across all three design docs (sm .25rem / DEFAULT .5rem /
// md .75rem / lg 1rem / xl 1.5rem / full 9999px). 1rem = 16dp @ default
// density scale.
//
// Screens use `MaterialTheme.shapes.*` directly for standard components,
// and the `Pill` shape below for buttons, chips and status tags — matching
// the fully-rounded buttons/chips visible throughout the approved Stitch
// screens (login, onboarding, "Aceitar e Continuar", case-status chips…).
// ============================================================================

val AppShapes = Shapes(
  extraSmall = RoundedCornerShape(4.dp), // sm  — .25rem
  small = RoundedCornerShape(8.dp), // DEFAULT — .5rem
  medium = RoundedCornerShape(12.dp), // md — .75rem
  large = RoundedCornerShape(16.dp), // lg — 1rem — cards, modals, sheets
  extraLarge = RoundedCornerShape(24.dp), // xl — 1.5rem — large surfaces
)

/** Fully-rounded ("pill") shape — buttons, chips, status tags, search fields. */
val Pill = CircleShape

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set false to ensure our custom brand identity colors shine
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = AppShapes,
    content = content,
  )
}

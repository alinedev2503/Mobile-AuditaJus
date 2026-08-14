package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF004AC6),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFF2563EB),
  onPrimaryContainer = Color(0xFFEEEFF1),
  secondary = Color(0xFF565E74),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFDAE2FD),
  onSecondaryContainer = Color(0xFF131B2E),
  background = Color(0xFFF8F9FF),
  onBackground = Color(0xFF0B1C30),
  surface = Color(0xFFF8F9FF),
  onSurface = Color(0xFF0B1C30),
  surfaceVariant = Color(0xFFE5EEFF),
  onSurfaceVariant = Color(0xFF434655),
  surfaceContainerLowest = Color(0xFFFFFFFF),
  surfaceContainerLow = Color(0xFFEFF4FF),
  surfaceContainer = Color(0xFFE5EEFF),
  surfaceContainerHigh = Color(0xFFDCE9FF),
  surfaceContainerHighest = Color(0xFFD3E4FE),
  outline = Color(0xFF737686),
  outlineVariant = Color(0xFFC3C6D7),
  error = BentoError,
  errorContainer = BentoErrorContainer,
  onErrorContainer = BentoOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFB4C5FF),
  onPrimary = Color(0xFF002A78),
  primaryContainer = Color(0xFF2563EB),
  onPrimaryContainer = Color(0xFFEEEFF1),
  secondary = Color(0xFFB9C7E0),
  onSecondary = Color(0xFF233144),
  secondaryContainer = Color(0xFF3C4A5E),
  onSecondaryContainer = Color(0xFFABB9D2),
  background = Color(0xFF101415),
  onBackground = Color(0xFFE0E3E5),
  surface = Color(0xFF101415),
  onSurface = Color(0xFFE0E3E5),
  surfaceVariant = Color(0xFF1D2022),
  onSurfaceVariant = Color(0xFFC3C6D7),
  surfaceContainerLowest = Color(0xFF0B0F10),
  surfaceContainerLow = Color(0xFF191C1E),
  surfaceContainer = Color(0xFF1D2022),
  surfaceContainerHigh = Color(0xFF272A2C),
  surfaceContainerHighest = Color(0xFF323537),
  outline = Color(0xFF8D90A0),
  outlineVariant = Color(0xFF434655),
  error = Color(0xFFFFB4AB),
  errorContainer = Color(0xFF93000A),
  onErrorContainer = Color(0xFFFFDAD6)
)

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
    content = content
  )
}


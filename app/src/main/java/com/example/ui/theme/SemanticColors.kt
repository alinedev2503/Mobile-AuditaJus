package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Content (foreground) + container (background) color pair for a semantic tone. */
data class SemanticColors(val content: Color, val container: Color)

/** Amber — offline banners, ratings/stars, "aguardando" tags. */
@Composable
fun warningColors(): SemanticColors {
    val dark = isSystemInDarkTheme()
    return SemanticColors(
        content = if (dark) WarningDark else WarningLight,
        container = if (dark) WarningContainerDark else WarningContainerLight,
    )
}

/** Green — success confirmations, completed states, positive tips. */
@Composable
fun successColors(): SemanticColors {
    val dark = isSystemInDarkTheme()
    return SemanticColors(
        content = if (dark) SuccessDark else SuccessLight,
        container = if (dark) SuccessContainerDark else SuccessContainerLight,
    )
}

/** Blue — informational tips, "processando" states. */
@Composable
fun infoColors(): SemanticColors {
    val dark = isSystemInDarkTheme()
    return SemanticColors(
        content = if (dark) InfoDark else InfoLight,
        container = if (dark) InfoContainerDark else InfoContainerLight,
    )
}

/** Violet — filed/protocolado states, "legal" category tags. */
@Composable
fun filedColors(): SemanticColors {
    val dark = isSystemInDarkTheme()
    return SemanticColors(
        content = if (dark) FiledDark else FiledLight,
        container = if (dark) FiledContainerDark else FiledContainerLight,
    )
}

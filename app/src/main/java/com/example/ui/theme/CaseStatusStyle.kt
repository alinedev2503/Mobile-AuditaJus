package com.example.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Visual representation of a [CaseEntity.status] value: label, foreground
 * (content) color, background (container) color and icon for a status chip.
 *
 * Single source of truth for status styling — replaces the three copies of
 * this `when` block that used to live in AuditsTimelineSection, MyCasesScreen
 * and HomeScreen with hand-picked hex values that drifted from each other and
 * never adapted to dark mode.
 */
data class CaseStatusVisual(
    val label: String,
    val contentColor: Color,
    val containerColor: Color,
    val icon: ImageVector,
)

@Composable
fun caseStatusVisual(status: String): CaseStatusVisual {
    val dark = MaterialTheme.colorScheme.background == DarkBackground
    return when (status) {
        "UPLOAD" -> CaseStatusVisual(
            label = "Em Envio",
            contentColor = if (dark) WarningDark else WarningLight,
            containerColor = if (dark) WarningContainerDark else WarningContainerLight,
            icon = Icons.Default.CloudUpload,
        )
        "ANALYSING" -> CaseStatusVisual(
            label = "Analisando IA",
            contentColor = if (dark) InfoDark else InfoLight,
            containerColor = if (dark) InfoContainerDark else InfoContainerLight,
            icon = Icons.Default.AutoAwesome,
        )
        "PDF_READY" -> CaseStatusVisual(
            label = "PDF Pronto",
            contentColor = if (dark) SuccessDark else SuccessLight,
            containerColor = if (dark) SuccessContainerDark else SuccessContainerLight,
            icon = Icons.Default.CheckCircle,
        )
        "SENT_TO_COURT" -> CaseStatusVisual(
            label = "Protocolado JEC",
            contentColor = if (dark) FiledDark else FiledLight,
            containerColor = if (dark) FiledContainerDark else FiledContainerLight,
            icon = Icons.Default.AccountBalance,
        )
        else -> CaseStatusVisual(
            label = "Pendente",
            contentColor = if (dark) NeutralDark else NeutralLight,
            containerColor = if (dark) NeutralContainerDark else NeutralContainerLight,
            icon = Icons.Default.Schedule,
        )
    }
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CaseEntity
import com.example.ui.theme.Pill
import com.example.ui.theme.caseStatusVisual
import com.example.ui.theme.successColors

@Composable
fun RecentAuditSummaryCard(
    caseEntity: CaseEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("recent_audit_card_${caseEntity.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Category Icon, Title and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val (iconEmoji, iconBg) = when (caseEntity.category) {
                        "Energia" -> Pair("⚡", MaterialTheme.colorScheme.primaryContainer)
                        "Telefonia", "Telecom" -> Pair("📱", MaterialTheme.colorScheme.secondaryContainer)
                        "Empréstimos Bancários", "Bancário" -> Pair("🏦", MaterialTheme.colorScheme.tertiaryContainer)
                        "Repetição em Dobro" -> Pair("⚖️", MaterialTheme.colorScheme.primaryContainer)
                        "FGTS" -> Pair("💼", MaterialTheme.colorScheme.surfaceContainerHigh)
                        else -> Pair("📄", MaterialTheme.colorScheme.surfaceContainerHigh)
                    }

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = iconBg,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = iconEmoji, fontSize = 20.sp)
                        }
                    }

                    Column {
                        Text(
                            text = caseEntity.title.ifBlank { "Auditoria #${caseEntity.id}" },
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = caseEntity.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            if (caseEntity.confidenceScore > 0 && caseEntity.rawGeminiJson.isNotBlank()) {
                                val success = successColors()
                                Surface(
                                    shape = Pill,
                                    color = success.container
                                ) {
                                    Text(
                                        text = "IA ${(caseEntity.confidenceScore * 100).toInt()}%",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = success.content
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                StatusBadge(status = caseEntity.status)
            }

            // Summary of Identified Abuse or Calculation breakdown stored in Room
            if (caseEntity.identifiedAbuseSummary.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = caseEntity.identifiedAbuseSummary,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Values & Date Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Valor Recuperável",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "R$ %,.2f".format(caseEntity.subtotalUpdated),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = caseEntity.date,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Abrir detalhes",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val visual = caseStatusVisual(status)

    Surface(
        color = visual.containerColor,
        shape = Pill
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = visual.icon,
                contentDescription = null,
                tint = visual.contentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = visual.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = visual.contentColor,
                    fontSize = 10.sp
                )
            )
        }
    }
}

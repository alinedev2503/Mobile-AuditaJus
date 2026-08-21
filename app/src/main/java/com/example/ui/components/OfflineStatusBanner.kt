package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.warningColors

@Composable
fun OfflineStatusBanner(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    onForceSimulateToggle: (() -> Unit)? = null
) {
    val warning = warningColors()
    AnimatedVisibility(
        visible = !isOnline,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("offline_status_banner_warning"),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = warning.container),
            border = BorderStroke(1.dp, warning.content.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = warning.content,
                        shape = CircleShape,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Modo Offline",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Modo Offline Ativo",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = warning.content
                            )
                        )
                        Text(
                            text = "Acessando auditorias e cálculos salvos no Room local",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = warning.content,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    color = warning.container,
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, warning.content.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = warning.content,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "ROOM LOCAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = warning.content,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

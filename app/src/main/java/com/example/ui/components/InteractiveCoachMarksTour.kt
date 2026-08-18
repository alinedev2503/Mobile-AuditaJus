package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TourStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val targetHint: String
)

@Composable
fun InteractiveCoachMarksTour(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onFinishTour: () -> Unit
) {
    if (!isVisible) return

    val tourSteps = remember {
        listOf(
            TourStep(
                title = "1. Escolha a Categoria ou Contrato",
                description = "Selecione se sua auditoria é de Energia, Telecomunicações (SVA), Empréstimo Bancário ou Repetição em Dobro.",
                icon = Icons.Default.Category,
                targetHint = "Contratos, faturas de luz, extratos ou contas de telefone."
            ),
            TourStep(
                title = "2. Capture a Prova ou Fatura",
                description = "Tire uma foto nítida da fatura, boleto ou contrato onde conste o valor cobrado e a data da cobrança. O app lê os dados automaticamente.",
                icon = Icons.Default.CameraAlt,
                targetHint = "Dica: Garanta boa iluminação e foque nas linhas com valores."
            ),
            TourStep(
                title = "3. Auditoria e Cálculo Inteligente",
                description = "O Gemini IA identifica os encargos ilegais, dobra o valor cobrado (Art. 42 CDC) e calcula juros e correção monetária.",
                icon = Icons.Default.AutoAwesome,
                targetHint = "O resultado é armazenado localmente em seu aparelho."
            ),
            TourStep(
                title = "4. Baixe a Petição + Procuração",
                description = "Pronto! Você pode exportar o combo de Petição Inicial formatada e Procuração Ad Judicia para o JEC com timbre personalizado.",
                icon = Icons.Default.Description,
                targetHint = "Pronto para assinar e protocolar!"
            )
        )
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    val step = tourSteps[currentStepIndex]
    val isLastStep = currentStepIndex == tourSteps.size - 1

    // Semi-transparent overlay with pulse animation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .clickable { /* Prevent background clicks */ }
            .testTag("interactive_tour_coach_marks"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pulse spotlight icon container
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            // Coach mark dialog card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "PASSO ${currentStepIndex + 1} DE ${tourSteps.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp).testTag("tour_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Pular Tour",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    )

                    // Target hint badge
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFEAB308),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = step.targetHint,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    // Progress indicators & controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dots
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            tourSteps.indices.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(
                                            width = if (index == currentStepIndex) 20.dp else 8.dp,
                                            height = 8.dp
                                        )
                                        .clip(CircleShape)
                                        .background(
                                            if (index == currentStepIndex)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.outlineVariant
                                        )
                                )
                            }
                        }

                        // Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (currentStepIndex > 0) {
                                OutlinedButton(
                                    onClick = { currentStepIndex-- },
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Anterior", fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    if (isLastStep) {
                                        onFinishTour()
                                    } else {
                                        currentStepIndex++
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("tour_next_button")
                            ) {
                                Text(
                                    text = if (isLastStep) "Começar Agora" else "Próximo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (isLastStep) Icons.Default.Check else Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

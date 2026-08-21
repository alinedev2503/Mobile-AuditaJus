package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.Pill
import com.example.ui.theme.warningColors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GuidedTourStep(
    val stepNumber: Int,
    val tag: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badgeText: String,
    val highlightFeatures: List<String>,
    val lawReference: String
)

@Composable
fun LegalAuditGuidedTourDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onStartAuditNow: () -> Unit
) {
    if (!isVisible) return

    val steps = remember {
        listOf(
            GuidedTourStep(
                stepNumber = 1,
                tag = "EXTRAÇÃO & OCR",
                title = "1. Digitalização de Faturas e Contratos",
                description = "Envie uma foto de conta de luz, extrato bancário, contrato de financiamento ou print de conversa. O app faz a leitura automática dos encargos.",
                icon = Icons.Outlined.DocumentScanner,
                badgeText = "OCR & Câmera",
                highlightFeatures = listOf(
                    "Reconhecimento ótico com alta nitidez",
                    "Detecção de CNPJ da empresa ré e datas",
                    "Suporte a fotos da câmera ou PDF do banco"
                ),
                lawReference = "Art. 373, I, CPC — Prova documental inicial"
            ),
            GuidedTourStep(
                stepNumber = 2,
                tag = "AUDITORIA GEMINI IA",
                title = "2. Identificação de Abusividades",
                description = "Nossa inteligência contábil treinada em Direito do Consumidor vasculha taxas ilegais como SVA em telecomunicações e juros acima da taxa BACEN.",
                icon = Icons.Outlined.AutoAwesome,
                badgeText = "Gemini 2.5 AI",
                highlightFeatures = listOf(
                    "Identificação de tarifas não contratadas",
                    "Comparação com a taxa média BACEN mensal",
                    "Cálculo de probabilidade de procedência no JEC"
                ),
                lawReference = "Art. 39, V e 51, IV, CDC — Práticas abusivas"
            ),
            GuidedTourStep(
                stepNumber = 3,
                tag = "LIQUIDAÇÃO CONTÁBIL",
                title = "3. Dobra Legal & Correção Monetária",
                description = "O sistema aplica automaticamente a Repetição em Dobro (Art. 42 CDC) e liquida valores históricos com correção INPC e juros moratórios de 1% a.m.",
                icon = Icons.Outlined.Calculate,
                badgeText = "Art. 42 CDC",
                highlightFeatures = listOf(
                    "Repetição em Dobro (STJ EAREsp 676.608/RJ)",
                    "Correção monetária oficial tabela TJ",
                    "Sugestão de indenização por Danos Morais"
                ),
                lawReference = "Súmula 297 STJ e Súmula 54 STJ"
            ),
            GuidedTourStep(
                stepNumber = 4,
                tag = "EXPORTAÇÃO & PETICIONAMENTO",
                title = "4. Petição Inicial + Procuração Prontas",
                description = "Gere com 1 clique a Petição Inicial completa estruturada em Fatos, Fundamentos e Pedidos, com timbre personalizado da sua advocacia para o JEC.",
                icon = Icons.Outlined.Description,
                badgeText = "JEC Sem Custas",
                highlightFeatures = listOf(
                    "Petição Inicial formatada em PDF padrão tribunal",
                    "Procuração Ad Judicia com poderes específicos",
                    "Timbre customizado com seu número de OAB"
                ),
                lawReference = "Lei 9.099/95 — Ações até 20 salários mínimos"
            )
        )
    }

    var currentStepIndex by remember { mutableStateOf(0) }
    val currentStep = steps[currentStepIndex]
    val isLast = currentStepIndex == steps.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable { /* Prevent dismiss on outside tap */ }
            .testTag("guided_tour_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated Header Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
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
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = currentStep.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Main Tour Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Bar with Step Tag and Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "PASSO ${currentStep.stepNumber} DE ${steps.size}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.6.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = currentStep.badgeText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp).testTag("tour_dialog_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Pular Tour",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Step Title
                    Text(
                        text = currentStep.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Step Description
                    Text(
                        text = currentStep.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )

                    // Highlights List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentStep.highlightFeatures.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    // Law Reference Pill
                    val warning = warningColors()
                    Surface(
                        color = warning.container,
                        shape = Pill,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = warning.content,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = currentStep.lawReference,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = warning.content,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Progress Dots & Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dots
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            steps.indices.forEach { idx ->
                                Box(
                                    modifier = Modifier
                                        .size(
                                            width = if (idx == currentStepIndex) 22.dp else 7.dp,
                                            height = 7.dp
                                        )
                                        .clip(CircleShape)
                                        .background(
                                            if (idx == currentStepIndex)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.outlineVariant
                                        )
                                )
                            }
                        }

                        // Navigation Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (currentStepIndex > 0) {
                                OutlinedButton(
                                    onClick = { currentStepIndex-- },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text("Voltar", fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    if (isLast) {
                                        onStartAuditNow()
                                    } else {
                                        currentStepIndex++
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("tour_step_next_btn")
                            ) {
                                Text(
                                    text = if (isLast) "Começar Auditoria" else "Próximo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (isLast) Icons.Default.RocketLaunch else Icons.Default.ArrowForward,
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

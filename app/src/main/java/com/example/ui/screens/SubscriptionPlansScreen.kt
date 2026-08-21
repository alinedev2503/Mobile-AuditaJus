package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

enum class BillingCycle {
    MONTHLY, ANNUAL
}

enum class SubscriptionTier {
    FREE, PRO, ENTERPRISE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPlansScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBillingCycle by remember { mutableStateOf(BillingCycle.ANNUAL) }
    var selectedTier by remember { mutableStateOf(SubscriptionTier.PRO) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Planos & Assinatura",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("subscription_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { showSuccessDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("subscribe_action_button"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (selectedTier) {
                                SubscriptionTier.FREE -> "Continuar no Plano Grátis"
                                SubscriptionTier.PRO -> "Assinar Plano Pro (7 dias grátis)"
                                SubscriptionTier.ENTERPRISE -> "Falar com Consultor Empresarial"
                            },
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 16.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Garantia de 7 dias ou seu dinheiro de volta • Cancele quando quiser",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            // Header Title & Subtitle
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "ACELERE SEUS PROCESSOS NO JEC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Text(
                        text = "Potencialize sua Advocacia com Auditoria Pericial por IA",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gere petições completas com cálculo de juros, INPC e repetição em dobro em menos de 1 minuto.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                }
            }

            // Billing Cycle Toggle (Monthly vs Annual with 20% OFF tag)
            item {
                BillingCycleSelector(
                    selectedCycle = selectedBillingCycle,
                    onCycleChange = { selectedBillingCycle = it }
                )
            }

            // PLAN 1: PRO TIER (Featured Bento Card)
            item {
                PlanCard(
                    tier = SubscriptionTier.PRO,
                    title = "Advogado Pro",
                    badge = "MAIS POPULAR ⭐",
                    price = if (selectedBillingCycle == BillingCycle.ANNUAL) "R$ 49,90" else "R$ 69,90",
                    billingPeriod = if (selectedBillingCycle == BillingCycle.ANNUAL) "/mês (faturado anualmente)" else "/mês",
                    description = "Ideal para advogados e correspondentes que demandam cálculo e peticionamento ágil.",
                    isSelected = selectedTier == SubscriptionTier.PRO,
                    onSelect = { selectedTier = SubscriptionTier.PRO },
                    features = listOf(
                        "Auditorias Periciais Ilimitadas por IA",
                        "Exportação de Petição em PDF e DOCX (Word)",
                        "Cálculo automático de INPC + Juros 1% a.m.",
                        "Repetição do Indébito (Art. 42 CDC - Em Dobro)",
                        "Personalização de Timbre e OAB nas Petições",
                        "Assistente Jurídico Gemini Pro 24/7",
                        "Sincronização em Nuvem e Backup Automático"
                    ),
                    isFeatured = true
                )
            }

            // PLAN 2: FREE / STARTER TIER
            item {
                PlanCard(
                    tier = SubscriptionTier.FREE,
                    title = "Iniciante / Cidadão",
                    badge = "GRÁTIS",
                    price = "R$ 0,00",
                    billingPeriod = "/sempre grátis",
                    description = "Para cidadãos que desejam ingressar com ação própria no JEC.",
                    isSelected = selectedTier == SubscriptionTier.FREE,
                    onSelect = { selectedTier = SubscriptionTier.FREE },
                    features = listOf(
                        "Até 2 auditorias de faturas por mês",
                        "Cálculo básico de danos materiais",
                        "Exportação de Petição em PDF com marca d'água",
                        "Acesso aos Guias Jurídicos Essenciais"
                    ),
                    isFeatured = false
                )
            }

            // PLAN 3: ESCRITÓRIO / ENTERPRISE
            item {
                PlanCard(
                    tier = SubscriptionTier.ENTERPRISE,
                    title = "Banca de Advocacia",
                    badge = "MULTI-USUÁRIO",
                    price = if (selectedBillingCycle == BillingCycle.ANNUAL) "R$ 149,90" else "R$ 199,90",
                    billingPeriod = if (selectedBillingCycle == BillingCycle.ANNUAL) "/mês (faturado anualmente)" else "/mês",
                    description = "Para escritórios com múltiplos advogados, estagiários e alto volume de causas consumeristas.",
                    isSelected = selectedTier == SubscriptionTier.ENTERPRISE,
                    onSelect = { selectedTier = SubscriptionTier.ENTERPRISE },
                    features = listOf(
                        "Tudo do Plano Pro incluído",
                        "Até 5 licenças simultâneas para o escritório",
                        "Painel gerencial de produtividade da equipe",
                        "Modelos de petição customizáveis em lote",
                        "Suporte prioritário via WhatsApp com perito contábil"
                    ),
                    isFeatured = false
                )
            }

            // FAQ Bento Section
            item {
                FaqBentoCard()
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "Assinatura Confirmada!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Parabéns! O seu plano Pro foi ativado com sucesso. Você agora tem acesso ilimitado a todas as auditorias periciais e peças jurídicas.",
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Começar a Usar")
                    }
                }
            )
        }
    }
}

@Composable
fun BillingCycleSelector(
    selectedCycle: BillingCycle,
    onCycleChange: (BillingCycle) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Monthly Button
            Surface(
                color = if (selectedCycle == BillingCycle.MONTHLY) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCycleChange(BillingCycle.MONTHLY) }
            ) {
                Text(
                    text = "Mensal",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (selectedCycle == BillingCycle.MONTHLY) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Annual Button with Discount Tag
            Surface(
                color = if (selectedCycle == BillingCycle.ANNUAL) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .weight(1.3f)
                    .clickable { onCycleChange(BillingCycle.ANNUAL) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Anual",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedCycle == BillingCycle.ANNUAL) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (selectedCycle == BillingCycle.ANNUAL) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "-30% OFF",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (selectedCycle == BillingCycle.ANNUAL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    tier: SubscriptionTier,
    title: String,
    badge: String,
    price: String,
    billingPeriod: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    features: List<String>,
    isFeatured: Boolean
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isFeatured -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("plan_card_${tier.name.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFeatured) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                MaterialTheme.colorScheme.surfaceContainerLowest
            }
        ),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFeatured || isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Row: Title + Badge + Radio Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = if (isFeatured) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isFeatured) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Price Row
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isFeatured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = billingPeriod,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Features Checklist
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    if (isFeatured) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isFeatured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FaqBentoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Dúvidas Frequentes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            FaqItem(
                question = "Como funciona o teste gratuito de 7 dias?",
                answer = "Você pode usar todos os recursos ilimitados do plano Pro por 7 dias. Se não gostar, cancele antes do prazo sem nenhuma cobrança."
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            FaqItem(
                question = "Os cálculos possuem validade jurídica no JEC?",
                answer = "Sim! Todas as memórias de cálculo utilizam os índices oficiais de correção monetária (INPC) e taxa legal de juros de 1% ao mês conforme o Código Civil e CDC."
            )
        }
    }
}

@Composable
fun FaqItem(
    question: String,
    answer: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

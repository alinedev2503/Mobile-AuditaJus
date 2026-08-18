package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CaseEntity
import com.example.ui.MainViewModel
import com.example.ui.components.OfflineStatusBanner
import com.example.util.LegalCalculationEngine
import com.example.util.PdfExportManager
import com.example.util.rememberIsOnline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationDetailsScreen(
    viewModel: MainViewModel,
    caseId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToPetition: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isOnline by rememberIsOnline()
    val caseEntity by viewModel.currentCase.collectAsState()
    val casePhotos by viewModel.currentCasePhotos.collectAsState()

    var showAdjustDialog by remember { mutableStateOf(false) }
    var showTypeSelectorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(caseId) {
        viewModel.selectCase(caseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhamento do Cálculo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("calc_details_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            caseEntity?.let { current ->
                                viewModel.sharePetitionPdf(
                                    context = context,
                                    caseEntity = current,
                                    exportType = PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO
                                )
                            }
                        },
                        modifier = Modifier.testTag("calc_details_share_pdf_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartilhar Laudo e Cálculo PDF",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { showTypeSelectorDialog = true },
                        modifier = Modifier.testTag("calc_type_selector_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Mudar Tipo de Cálculo",
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToPetition(caseId) },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("proceed_to_petition_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = "Prosseguir para Petição",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                caseEntity?.let { current ->
                                    viewModel.sharePetitionPdf(
                                        context = context,
                                        caseEntity = current,
                                        exportType = PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(0.9f)
                                .height(48.dp)
                                .testTag("calc_details_bottom_share_button"),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Compartilhar",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showAdjustDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("adjust_values_button"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = "Ajustar Valores Manualmente",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        caseEntity?.let { current ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                // Dynamic Offline Connection Status Warning Banner
                item {
                    OfflineStatusBanner(isOnline = isOnline)
                }

                // Card 0: Selector Banner of Calculation Mode (Repetição em dobro / Bancário / Telecom / Padrão)
                item {
                    CalculationTypeSwitcherCard(
                        current = current,
                        onOpenSelector = { showTypeSelectorDialog = true }
                    )
                }

                // Card 1: Dano Material & Decomposição
                item {
                    MaterialDamageCard(current = current, casePhotos = casePhotos)
                }

                // Card 2: Se for Empréstimo Bancário, mostrar Comparativo de Taxas BACEN vs Contrato
                if (current.calculationType == "EMPRESTIMO_BANCARIO") {
                    item {
                        BankRatesComparativeCard(current = current)
                    }
                }

                // Card 3: Se for Telecom, mostrar Card de Serviços de Terceiros / SVA
                if (current.calculationType == "TELECOM_SERVICOS") {
                    item {
                        TelecomSvaCard(current = current)
                    }
                }

                // Card 4: Atualização Monetária e Juros (Receipt Style)
                item {
                    MonetaryCorrectionReceiptCard(current = current)
                }

                // Card 5: Danos Morais Sugeridos (Highlight Card)
                item {
                    MoralDamagesHighlightCard(current = current)
                }

                // Card 6: Fundamentação Legal
                item {
                    LegalBasisCard(current = current)
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primaryContainer)
            }
        }

        if (showAdjustDialog && caseEntity != null) {
            AdjustCalculationsDialog(
                current = caseEntity!!,
                onDismiss = { showAdjustDialog = false },
                onSave = { mat, inpc, juros, moral ->
                    viewModel.updateManualValues(caseEntity!!, mat, inpc, juros, moral)
                    showAdjustDialog = false
                }
            )
        }

        if (showTypeSelectorDialog && caseEntity != null) {
            CalculationTypeSelectorDialog(
                current = caseEntity!!,
                onDismiss = { showTypeSelectorDialog = false },
                onSelectType = { type, months, bankRate, bacenRate ->
                    viewModel.applyCalculationType(
                        caseEntity = caseEntity!!,
                        calculationType = type,
                        months = months,
                        bankContractRate = bankRate,
                        bacenAverageRate = bacenRate
                    )
                    showTypeSelectorDialog = false
                }
            )
        }
    }
}

@Composable
fun CalculationTypeSwitcherCard(
    current: CaseEntity,
    onOpenSelector: () -> Unit
) {
    val modeTitle = when (current.calculationType) {
        "REPETICAO_DOBRO" -> "Repetição em Dobro (2x)"
        "EMPRESTIMO_BANCARIO" -> "Revisional de Juros Bancários"
        "TELECOM_SERVICOS" -> "Telecom & Serviços de Terceiros (SVA)"
        else -> "Cálculo Padrão Atualizado"
    }

    val modeIcon = when (current.calculationType) {
        "REPETICAO_DOBRO" -> Icons.Default.Filter2
        "EMPRESTIMO_BANCARIO" -> Icons.Default.AccountBalance
        "TELECOM_SERVICOS" -> Icons.Default.PhoneIphone
        else -> Icons.Default.Calculate
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSelector() }
            .testTag("calc_type_switcher_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = modeIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = modeTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Toque para alterar a metodologia de cálculo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Mudar",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BankRatesComparativeCard(current: CaseEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "COMPARATIVO DE TAXAS BANCÁRIAS (BACEN)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Taxa Contratada:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "%.2f%% a.m.".format(if (current.bankContractRate > 0) current.bankContractRate else 8.5),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    )
                }
                Column {
                    Text("Taxa Média BACEN:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "%.2f%% a.m.".format(if (current.bacenAverageRate > 0) current.bacenAverageRate else 2.1),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                }
                Column {
                    Text("Excesso Abusivo:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val diff = (if (current.bankContractRate > 0) current.bankContractRate else 8.5) - (if (current.bacenAverageRate > 0) current.bacenAverageRate else 2.1)
                    Text(
                        "+%.2f%%".format(maxOf(0.0, diff)),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Súmula 297/STJ e Recurso Especial 1.061.530/RS: Permite limitação à taxa média de mercado divulgada pelo Banco Central do Brasil para a mesma época da contratação.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun TelecomSvaCard(current: CaseEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneIphone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "EXPURGO DE SVA & PACOTES TELECOM",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("• Serviços de Terceiros / Bancas:", style = MaterialTheme.typography.bodyMedium)
                    Text("R$ %.2f".format(current.historicalValue * 0.4).replace(".", ","), fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("• Pacote de Dados Avulso Não Solicitado:", style = MaterialTheme.typography.bodyMedium)
                    Text("R$ %.2f".format(current.historicalValue * 0.6).replace(".", ","), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Repetição em Dobro (Art. 42 CDC):", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text("R$ %.2f".format(current.historicalValue * 2.0).replace(".", ","), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}

@Composable
fun MaterialDamageCard(
    current: CaseEntity,
    casePhotos: List<com.example.data.db.EvidencePhotoEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dano Material Base",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (current.isRepeticaoEmDobro) "Base calculada com repetição em dobro (2x)" else "Valores cobrados indevidamente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "R$ %.2f".format(if (current.isRepeticaoEmDobro) current.historicalValue * 2 else current.historicalValue).replace(".", ","),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            if (casePhotos.isEmpty()) {
                DamageItemRow(
                    icon = Icons.Default.ReceiptLong,
                    title = "Valor Principal Cobrado Indevidamente",
                    value = current.historicalValue * 0.5
                )
                Spacer(modifier = Modifier.height(8.dp))
                DamageItemRow(
                    icon = Icons.Default.Warning,
                    title = "Taxas e Encargos Acessórios Indevidos",
                    value = current.historicalValue * 0.5
                )
            } else {
                casePhotos.forEachIndexed { index, photo ->
                    DamageItemRow(
                        icon = if (index % 2 == 0) Icons.Default.ReceiptLong else Icons.Default.Warning,
                        title = photo.label,
                        value = if (photo.analyzedAmount > 0) photo.analyzedAmount else current.historicalValue / casePhotos.size
                    )
                    if (index < casePhotos.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DamageItemRow(
    icon: ImageVector,
    title: String,
    value: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "R$ %.2f".format(value).replace(".", ","),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MonetaryCorrectionReceiptCard(current: CaseEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Atualização Monetária e Juros",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Receipt Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(10.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReceiptRow(label = "Valor Histórico", value = "R$ %.2f".format(current.historicalValue).replace(".", ","))
                    if (current.isRepeticaoEmDobro) {
                        ReceiptRow(label = "Dobra Legal (Art. 42 CDC)", value = "2x (R$ %.2f)".format(current.historicalValue * 2).replace(".", ","))
                    }
                    ReceiptRow(label = "Correção Monetária (INPC)", value = "R$ %.2f".format(current.inpcCorrection).replace(".", ","))
                    ReceiptRow(label = "Juros Moratórios (1% a.m.)", value = "R$ %.2f".format(current.defaultInterest).replace(".", ","))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subtotal Atualizado",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "R$ %.2f".format(current.subtotalUpdated).replace(".", ","),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "* Período apurado: ${current.monthsCalculated} meses (Súmulas 43 e 54 STJ)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MoralDamagesHighlightCard(current: CaseEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Balance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "DANOS MORAIS SUGERIDOS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = "R$ %.2f".format(current.suggestedMoralDamages).replace(".", ","),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = "Jurisprudência dominante do JEC para ${current.category}: fixação razoável com finalidade pedagógica e punitiva pelo desvio produtivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun LegalBasisCard(current: CaseEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "FUNDAMENTAÇÃO LEGAL APLICADA",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            LegalItem(
                icon = Icons.Default.Gavel,
                title = current.legalBasis.ifBlank { "Art. 42, CDC | Súmula 297, STJ" },
                description = "Base jurídica parametrizada para o tipo de cálculo selecionado."
            )

            LegalItem(
                icon = Icons.Default.TrendingUp,
                title = "Súmula 43 e 54, STJ",
                description = "Incidência de correção monetária e juros a partir do evento danoso."
            )
        }
    }
}

@Composable
fun LegalItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CalculationTypeSelectorDialog(
    current: CaseEntity,
    onDismiss: () -> Unit,
    onSelectType: (String, Int, Double, Double) -> Unit
) {
    var selectedType by remember { mutableStateOf(current.calculationType) }
    var monthsStr by remember { mutableStateOf(current.monthsCalculated.toString()) }
    var bankRateStr by remember { mutableStateOf(if (current.bankContractRate > 0) current.bankContractRate.toString() else "8.5") }
    var bacenRateStr by remember { mutableStateOf(if (current.bacenAverageRate > 0) current.bacenAverageRate.toString() else "2.1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Metodologia de Cálculo",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Escolha o tipo de apuração jurídica aplicável:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Option 1: Padrão
                CalculationTypeOption(
                    title = "Cálculo Padrão (1x)",
                    description = "INPC + Juros 1% a.m.",
                    icon = Icons.Default.Calculate,
                    isSelected = selectedType == "PADRAO",
                    onSelect = { selectedType = "PADRAO" }
                )

                // Option 2: Repetição em Dobro
                CalculationTypeOption(
                    title = "Repetição em Dobro (2x)",
                    description = "Art. 42 CDC (Devolução em dobro da cobrança)",
                    icon = Icons.Default.Filter2,
                    isSelected = selectedType == "REPETICAO_DOBRO",
                    onSelect = { selectedType = "REPETICAO_DOBRO" }
                )

                // Option 3: Revisional Bancária
                CalculationTypeOption(
                    title = "Empréstimo Bancário (Revisional)",
                    description = "Expurgo de juros abusivos acima do BACEN",
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedType == "EMPRESTIMO_BANCARIO",
                    onSelect = { selectedType = "EMPRESTIMO_BANCARIO" }
                )

                // Option 4: Telecom
                CalculationTypeOption(
                    title = "Telecom & SVA",
                    description = "Serviços não autorizados em dobro (Anatel)",
                    icon = Icons.Default.PhoneIphone,
                    isSelected = selectedType == "TELECOM_SERVICOS",
                    onSelect = { selectedType = "TELECOM_SERVICOS" }
                )

                if (selectedType == "EMPRESTIMO_BANCARIO") {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bankRateStr,
                            onValueChange = { bankRateStr = it },
                            label = { Text("Taxa Contrato %") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bacenRateStr,
                            onValueChange = { bacenRateStr = it },
                            label = { Text("Taxa BACEN %") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                OutlinedTextField(
                    value = monthsStr,
                    onValueChange = { monthsStr = it },
                    label = { Text("Meses decorridos da cobrança") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val months = monthsStr.toIntOrNull() ?: 12
                    val bankRate = bankRateStr.replace(",", ".").toDoubleOrNull() ?: 8.5
                    val bacenRate = bacenRateStr.replace(",", ".").toDoubleOrNull() ?: 2.1
                    onSelectType(selectedType, months, bankRate, bacenRate)
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Aplicar Metodologia")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CalculationTypeOption(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun AdjustCalculationsDialog(
    current: CaseEntity,
    onDismiss: () -> Unit,
    onSave: (Double, Double, Double, Double) -> Unit
) {
    fun formatCurrency(input: String): String {
        val cleanString = input.replace(Regex("[^0-9]"), "")
        if (cleanString.isEmpty()) return "0,00"
        val parsed = cleanString.toDoubleOrNull() ?: 0.0
        return String.format(java.util.Locale("pt", "BR"), "%,.2f", parsed / 100)
    }

    var matStr by remember { mutableStateOf(formatCurrency(String.format(java.util.Locale.US, "%.2f", current.historicalValue).replace(".", ""))) }
    var inpcStr by remember { mutableStateOf(formatCurrency(String.format(java.util.Locale.US, "%.2f", current.inpcCorrection).replace(".", ""))) }
    var jurosStr by remember { mutableStateOf(formatCurrency(String.format(java.util.Locale.US, "%.2f", current.defaultInterest).replace(".", ""))) }
    var moralStr by remember { mutableStateOf(formatCurrency(String.format(java.util.Locale.US, "%.2f", current.suggestedMoralDamages).replace(".", ""))) }

    val parseCurrency = { str: String ->
        str.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
    }

    val isMatValid = parseCurrency(matStr) >= 0.0
    val isInpcValid = parseCurrency(inpcStr) >= 0.0
    val isJurosValid = parseCurrency(jurosStr) >= 0.0
    val isMoralValid = parseCurrency(moralStr) >= 0.0
    val isValid = isMatValid && isInpcValid && isJurosValid && isMoralValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustar Cálculos", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = matStr,
                    onValueChange = { matStr = formatCurrency(it) },
                    label = { Text("Valor Histórico Dano Material (R$)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = inpcStr,
                    onValueChange = { inpcStr = formatCurrency(it) },
                    label = { Text("Correção INPC (R$)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = jurosStr,
                    onValueChange = { jurosStr = formatCurrency(it) },
                    label = { Text("Juros Moratórios (R$)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = moralStr,
                    onValueChange = { moralStr = formatCurrency(it) },
                    label = { Text("Danos Morais Sugeridos (R$)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mat = parseCurrency(matStr)
                    val inpc = parseCurrency(inpcStr)
                    val juros = parseCurrency(jurosStr)
                    val moral = parseCurrency(moralStr)
                    onSave(mat, inpc, juros, moral)
                },
                enabled = isValid
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

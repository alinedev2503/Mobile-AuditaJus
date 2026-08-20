package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.CaseEntity
import com.example.ui.MainViewModel
import com.example.ui.components.OfflineStatusBanner
import com.example.ui.components.SignaturePad
import com.example.ui.components.SignatureState
import com.example.ui.components.rememberSignatureState
import com.example.util.PdfExportManager
import com.example.util.rememberIsOnline
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StructuredPetitionScreen(
    viewModel: MainViewModel,
    caseId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToLawyerCustomization: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isOnline by rememberIsOnline()
    val caseEntity by viewModel.currentCase.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current

    val lawyerSignatureState = rememberSignatureState()
    val clientSignatureState = rememberSignatureState()

    var selectedExportMode by remember { mutableStateOf(PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Combo, 1 = Laudo Pericial & Juros, 2 = Procuração, 3 = Petição

    var watermarkText by remember { mutableStateOf("LAUDO PERICIAL JURÍDICO") }
    var enableWatermark by remember { mutableStateOf(true) }

    var step1Done by remember { mutableStateOf(true) }
    var step2Done by remember { mutableStateOf(true) }
    var step3Done by remember { mutableStateOf(false) }
    var step4Done by remember { mutableStateOf(false) }

    var downloadSuccessMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(caseId) {
        viewModel.selectCase(caseId)
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null && caseEntity != null) {
            val generatedFile = viewModel.exportDocument(
                context = context,
                caseEntity = caseEntity!!,
                signatureBitmap = lawyerSignatureState.toBitmap(),
                clientSignatureBitmap = clientSignatureState.toBitmap(),
                exportType = selectedExportMode,
                watermarkText = watermarkText,
                showWatermark = enableWatermark
            )
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    generatedFile.inputStream().use { input ->
                        input.copyTo(out)
                    }
                }
                val docName = when (selectedExportMode) {
                    PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO -> "Petição + Procuração conjunta"
                    PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO -> "Laudo Pericial de Auditoria e Juros"
                    PdfExportManager.ExportDocumentType.PROCURACAO_ONLY -> "Procuração Ad Judicia"
                    PdfExportManager.ExportDocumentType.PETICAO_ONLY -> "Petição Inicial"
                }
                downloadSuccessMessage = "$docName salva com marca d'água estruturada com sucesso!"
            } catch (e: Exception) {
                downloadSuccessMessage = "Erro ao salvar o documento: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Exportação de PDFs Jurídicos",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Layout Estruturado com Marca d'Água & Juros",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("petition_screen_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToLawyerCustomization,
                        modifier = Modifier.testTag("petition_top_lawyer_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = "Personalizar Timbre e OAB",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        caseEntity?.let { current ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
            ) {
                // Dynamic Offline Connection Status Warning Banner
                item {
                    OfflineStatusBanner(isOnline = isOnline)
                }

                // Success banner
                downloadSuccessMessage?.let { msg ->
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                // Document Export Type Tabs (Combo vs Laudo Pericial vs Procuração vs Petição)
                item {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 8.dp,
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                selectedExportMode = PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO
                            },
                            text = { Text("Combo (2 em 1)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                selectedExportMode = PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO
                            },
                            text = { Text("Laudo & Juros", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = {
                                selectedTab = 2
                                selectedExportMode = PdfExportManager.ExportDocumentType.PROCURACAO_ONLY
                            },
                            text = { Text("Procuração", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = {
                                selectedTab = 3
                                selectedExportMode = PdfExportManager.ExportDocumentType.PETICAO_ONLY
                            },
                            text = { Text("Petição", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }

                // Watermark Configuration Card (Marca d'água personalizada)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrandingWatermark,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Marca d'Água de Segurança",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Switch(
                                    checked = enableWatermark,
                                    onCheckedChange = { enableWatermark = it }
                                )
                            }

                            if (enableWatermark) {
                                OutlinedTextField(
                                    value = watermarkText,
                                    onValueChange = { watermarkText = it },
                                    label = { Text("Texto da Marca d'Água no Fundo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("LAUDO PERICIAL JURÍDICO", "CÓPIA NÃO AUTORIZADA", "CONFIDENCIAL • OAB").forEach { suggestion ->
                                        SuggestionChip(
                                            onClick = { watermarkText = suggestion },
                                            label = { Text(suggestion, fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Timbre Profile Card Header
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLawyerCustomization() }
                            .testTag("lawyer_timbre_banner_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
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
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (userSettings.logoUri.isNotBlank()) {
                                    AsyncImage(
                                        model = userSettings.logoUri,
                                        contentDescription = "Logo",
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalance,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Text(
                                        text = if (userSettings.lawFirmName.isNotBlank()) userSettings.lawFirmName else "Silva & Associados Advocacia",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${userSettings.userName} • OAB/${userSettings.oabUf} nº ${userSettings.oabNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                // Document Preview Card Based on Selected Tab
                if (selectedExportMode == PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                            text = "Laudo Técnico de Auditoria",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                    Text("Layout Estruturado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }

                                Text(
                                    text = "RELATÓRIO DE CÁLCULO E JUROS MORATÓRIOS",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("• Valor Histórico: R$ %,.2f".format(current.historicalValue), fontSize = 13.sp)
                                        if (current.isRepeticaoEmDobro) {
                                            Text("• Repetição em Dobro (2x): R$ %,.2f".format(current.historicalValue * 2), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Text("• Atualização INPC: R$ %,.2f".format(current.inpcCorrection), fontSize = 13.sp)
                                        Text("• Juros Moratórios Legais (1% a.m. - ${current.monthsCalculated} meses): R$ %,.2f".format(current.defaultInterest), fontSize = 13.sp)
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                        Text("TOTAL LIQUIDADO: R$ %,.2f".format(current.subtotalUpdated), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons (Baixar PDF / Compartilhar)
                item {
                    val exportButtonLabel = when (selectedExportMode) {
                        PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO -> "Baixar Petição + Procuração"
                        PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO -> "Baixar Laudo de Auditoria (PDF)"
                        PdfExportManager.ExportDocumentType.PROCURACAO_ONLY -> "Baixar Procuração (PDF)"
                        PdfExportManager.ExportDocumentType.PETICAO_ONLY -> "Baixar Petição (PDF)"
                    }

                    val fileNameDefault = when (selectedExportMode) {
                        PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO -> "Peticao_e_Procuracao_${current.id}_${System.currentTimeMillis()}.pdf"
                        PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO -> "Laudo_Auditoria_Juros_${current.id}_${System.currentTimeMillis()}.pdf"
                        PdfExportManager.ExportDocumentType.PROCURACAO_ONLY -> "Procuracao_AdJudicia_${current.id}_${System.currentTimeMillis()}.pdf"
                        PdfExportManager.ExportDocumentType.PETICAO_ONLY -> "Peticao_Inicial_${current.id}_${System.currentTimeMillis()}.pdf"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                createDocumentLauncher.launch(fileNameDefault)
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(52.dp)
                                .testTag("download_petition_pdf_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(exportButtonLabel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.sharePetitionPdf(
                                    context = context,
                                    caseEntity = current,
                                    signatureBitmap = lawyerSignatureState.toBitmap(),
                                    clientSignatureBitmap = clientSignatureState.toBitmap(),
                                    exportType = selectedExportMode,
                                    watermarkText = watermarkText,
                                    showWatermark = enableWatermark
                                )
                            },
                            modifier = Modifier
                                .weight(0.8f)
                                .height(52.dp)
                                .testTag("share_petition_pdf_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enviar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Interactive Checklist ("Próximos Passos para Entrada no JEC")
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("next_steps_checklist_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Próximos Passos para Protocolo no JEC",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            ChecklistItem(
                                title = "Documentos Pessoais Anexados (RG, CPF e Comprovante de Residência)",
                                isChecked = step1Done,
                                onCheckedChange = { step1Done = it }
                            )

                            ChecklistItem(
                                title = "Procuração Ad Judicia Assinada pelo Cliente",
                                isChecked = step2Done,
                                onCheckedChange = { step2Done = it }
                            )

                            ChecklistItem(
                                title = "Petição Timbrada com Memória de Cálculo Inclusa",
                                isChecked = step3Done,
                                onCheckedChange = { step3Done = it }
                            )

                            ChecklistItem(
                                title = "Protocolar no Atermação do JEC Eletrônico ou Presencial",
                                isChecked = step4Done,
                                onCheckedChange = { step4Done = it }
                            )
                        }
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ChecklistItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

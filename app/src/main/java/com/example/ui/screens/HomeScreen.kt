package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.data.db.CaseEntity
import com.example.data.db.EvidencePhotoEntity
import com.example.ui.MainViewModel
import com.example.ui.components.CameraCaptureView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToCalculation: (Long) -> Unit,
    onNavigateToPetition: (Long) -> Unit,
    onNavigateToGuides: () -> Unit,
    onOpenAiAssistant: () -> Unit,
    onNavigateToAllCases: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cases by viewModel.cases.collectAsState()
    val activeCase by viewModel.currentCase.collectAsState()
    val casePhotos by viewModel.currentCasePhotos.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current

    var showTutorial by remember { mutableStateOf(false) }
    if (showTutorial) {
        DocumentPhotoTutorialDialog(onDismiss = { showTutorial = false })
    }

    var showCamera by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showCamera = true
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            activeCase?.let { caseItem ->
                viewModel.addEvidencePhoto(caseItem.id, "Comprovante Anexado", selectedUri.toString())
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CONTADOR & AUDITOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "AuditaJus",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenAiAssistant,
                        modifier = Modifier.testTag("ai_assistant_button")
                    ) {
                        BadgedBox(badge = { Badge { Text("IA") } }) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = "Assistente IA Gemini",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userSettings.avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = userSettings.avatarUrl,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userSettings.userName.take(2).uppercase().ifBlank { "JP" },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Bento Card: Gemini AI Scanner
            item {
                BentoHeroScannerCard(
                    isAnalyzing = isAnalyzing,
                    activeCaseTitle = activeCase?.title ?: "Novo Caso no JEC",
                    onStartScanner = {
                        activeCase?.let { viewModel.triggerGeminiAnalysis(it.id) } ?: onOpenAiAssistant()
                    }
                )
            }

            // Bento 2-Column Grid Row (Prazos Críticos + Recuperação Est.)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bento Tile 1: Deadlines / Prazos
                    BentoTileDeadlines(
                        modifier = Modifier.weight(1f)
                    )

                    // Bento Tile 2: Estimated Recovery
                    val totalEst = activeCase?.let { it.subtotalUpdated + it.suggestedMoralDamages } ?: 12450.00
                    BentoTileEstimatedValue(
                        estimatedValue = totalEst,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Bento Stepper & Active Case Status Bar
            item {
                activeCase?.let { current ->
                    BentoActiveCaseCard(
                        caseEntity = current,
                        isAnalyzing = isAnalyzing,
                        onAnalyzeClick = { viewModel.triggerGeminiAnalysis(current.id) },
                        onViewDetails = {
                            if (current.status == "PDF_READY" || current.status == "SENT_TO_COURT") {
                                onNavigateToPetition(current.id)
                            } else {
                                onNavigateToCalculation(current.id)
                            }
                        }
                    )
                }
            }

            // Upload Evidence Bento Section
            item {
                AnexarProvasBentoSection(
                    casePhotos = casePhotos,
                    onAttachContract = {
                        activeCase?.let { viewModel.addEvidencePhoto(it.id, "Anexar Contrato") }
                    },
                    onAttachBill = {
                        activeCase?.let { viewModel.addEvidencePhoto(it.id, "Anexar Conta de Luz") }
                    },
                    onAttachWhatsapp = {
                        activeCase?.let { viewModel.addEvidencePhoto(it.id, "Anexar Conversa de WhatsApp") }
                    },
                    onShowTutorial = { showTutorial = true },
                    onOpenCamera = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            showCamera = true
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onCustomPick = {
                        photoPickerLauncher.launch("image/*")
                    }
                )
            }

            // Section Header: Auditorias Recentes
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Auditorias & Processos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    TextButton(onClick = onNavigateToAllCases, modifier = Modifier.testTag("view_all_cases_button")) {
                        Text("Ver Todos", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Recent Audits Items (Bento Card Style)
            items(cases.take(3)) { caseItem ->
                BentoAuditCaseCard(
                    caseEntity = caseItem,
                    onClick = {
                        viewModel.selectCase(caseItem.id)
                        onNavigateToCalculation(caseItem.id)
                    }
                )
            }

            // Help Banner Bento Tile
            item {
                HelpBannerBentoCard(onGuideClick = onNavigateToGuides)
            }
        }
        }
    }

    if (showCamera) {
        CameraCaptureView(
            onImageCaptured = { uri ->
                showCamera = false
                activeCase?.let { caseItem ->
                    viewModel.addEvidencePhoto(caseItem.id, "Foto da Câmera", uri.toString())
                }
            },
            onCancel = { showCamera = false }
        )
    }
}
@Composable
fun BentoHeroScannerCard(
    isAnalyzing: Boolean,
    activeCaseTitle: String,
    onStartScanner: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_hero_scanner_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "✨",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = "Gemini AI Ativo",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Nova Análise de Provas",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Digitalize faturas, contratos ou conversas de WhatsApp para cálculo instantâneo no JEC.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.85f)
                    )
                )
            }

            Button(
                onClick = onStartScanner,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("bento_hero_scanner_button"),
                enabled = !isAnalyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Auditando com IA...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciar Scanner Jurídico", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BentoTileDeadlines(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(130.dp)
            .testTag("bento_tile_deadlines"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "⏳", fontSize = 20.sp)
                Text(
                    text = "03",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
                Text(
                    text = "PRAZOS CRÍTICOS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        letterSpacing = 0.8.sp,
                        fontSize = 9.sp
                    )
                )
            }

            Text(
                text = "Vencimento em 24h",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun BentoTileEstimatedValue(
    estimatedValue: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .testTag("bento_tile_estimated_value"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "📈", fontSize = 20.sp)
                Text(
                    text = "R$ %.2f".format(estimatedValue),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
                Text(
                    text = "RECUPERAÇÃO EST.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp,
                        fontSize = 9.sp
                    )
                )
            }

            // Progress bar indicator
            LinearProgressIndicator(
                progress = 0.75f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun BentoActiveCaseCard(
    caseEntity: CaseEntity,
    isAnalyzing: Boolean,
    onAnalyzeClick: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_active_case_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "📑", fontSize = 20.sp)
                        }
                    }

                    Column {
                        Text(
                            text = caseEntity.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "Status: ${caseEntity.status} • Atermação JEC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        )
                    }
                }

                TextButton(onClick = onViewDetails) {
                    Text(
                        text = "ABRIR",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // Stepper progress indicator
            val currentStep = when (caseEntity.status) {
                "UPLOAD" -> 1
                "ANALYSING" -> 2
                else -> 3
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BentoStepDot(step = 1, label = "Upload", currentStep = currentStep)
                Divider(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    color = if (currentStep >= 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    thickness = 3.dp
                )
                BentoStepDot(step = 2, label = "Análise IA", currentStep = currentStep)
                Divider(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    color = if (currentStep >= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    thickness = 3.dp
                )
                BentoStepDot(step = 3, label = "Petição PDF", currentStep = currentStep)
            }
        }
    }
}

@Composable
fun BentoStepDot(step: Int, label: String, currentStep: Int) {
    val isDone = currentStep > step
    val isActive = currentStep == step

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone || isActive -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "$step",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1
        )
    }
}

@Composable
fun AnexarProvasBentoSection(
    casePhotos: List<EvidencePhotoEntity>,
    onAttachContract: () -> Unit,
    onAttachBill: () -> Unit,
    onAttachWhatsapp: () -> Unit,
    onCustomPick: () -> Unit,
    onOpenCamera: () -> Unit,
    onShowTutorial: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upload_evidence_bento_dropzone"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Anexar Provas ao Caso",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Row {
                    IconButton(onClick = onShowTutorial, modifier = Modifier.testTag("show_photo_tutorial_button")) {
                        Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = "Tutorial de Fotos", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onOpenCamera) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Tirar Foto")
                    }
                    IconButton(onClick = onCustomPick) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Adicionar Foto")
                    }
                }
            }

            // Quick Pill Action Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAttachContract,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).testTag("attach_contract_button")
                ) {
                    Text("📄 Contrato", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                OutlinedButton(
                    onClick = onAttachBill,
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).testTag("attach_bill_button")
                ) {
                    Text("⚡ Conta Luz", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onAttachWhatsapp,
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).testTag("attach_whatsapp_button")
                ) {
                    Text("💬 WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (casePhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                casePhotos.forEach { photo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerLowest,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = photo.label,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "R$ %.2f".format(photo.analyzedAmount),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BentoAuditCaseCard(
    caseEntity: CaseEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("audit_case_item_${caseEntity.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "📁", fontSize = 18.sp)
                        }
                    }
                    Column {
                        Text(
                            text = caseEntity.processNumber ?: "Processo #${caseEntity.id}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = caseEntity.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                StatusBadge(status = caseEntity.status)
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Criado em: ${caseEntity.date}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Abrir caso",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, text) = when (status) {
        "PDF_READY" -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "PDF Pronto")
        "SENT_TO_COURT" -> Triple(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.colorScheme.onSurface, "Enviado JEC")
        "ANALYSING" -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Em Análise")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Pendente")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun HelpBannerBentoCard(onGuideClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("help_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "💡", fontSize = 24.sp)
                Text(
                    text = "Dúvidas sobre o JEC ou cálculo de danos morais? Confira nossos tutoriais.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onGuideClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("view_help_guide_button")
            ) {
                Text("Ver Guias", fontSize = 12.sp)
            }
        }
    }
}


@Composable
fun DocumentPhotoTutorialDialog(onDismiss: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    
    val pages = listOf(
        Pair("1. Iluminação e Reflexos", "Posicione o documento em local bem iluminado, de preferência luz natural. Evite reflexos de flash que escondam números ou textos importantes."),
        Pair("2. Enquadramento Completo", "Tire a foto de cima para baixo. Certifique-se de que todas as bordas do documento (fatura, contrato) apareçam na imagem sem cortes."),
        Pair("3. Foco e Nitidez", "Toque na tela do celular para focar antes de capturar. O texto deve estar nítido e fácil de ler. Se estiver embaçado, a IA não conseguirá ler os valores."),
        Pair("4. Prints de Conversas", "Ao enviar prints do WhatsApp, mostre claramente as datas, o nome ou número do contato e o contexto da promessa ou cobrança abusiva.")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Dicas para fotos de documentos", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = pages[currentPage].first,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = pages[currentPage].second,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSecondaryContainer)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (currentPage == index) 10.dp else 8.dp)
                                .background(
                                    if (currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("tutorial_next_button")
            ) {
                Text(if (currentPage < pages.size - 1) "Próximo" else "Entendi")
            }
        },
        dismissButton = {
            if (currentPage > 0) {
                TextButton(onClick = { currentPage-- }, modifier = Modifier.testTag("tutorial_prev_button")) {
                    Text("Voltar")
                }
            }
        }
    )
}

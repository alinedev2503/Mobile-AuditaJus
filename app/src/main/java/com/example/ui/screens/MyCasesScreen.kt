package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CaseEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCasesScreen(
    viewModel: MainViewModel,
    onNavigateToCalculation: (Long) -> Unit,
    onNavigateToPetition: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val cases by viewModel.cases.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedCaseFilter.collectAsState()
    var showNewCaseDialog by remember { mutableStateOf(false) }

    val filteredCases = cases.filter { caseEntity ->
        val matchesQuery = caseEntity.title.contains(searchQuery, ignoreCase = true) ||
                caseEntity.category.contains(searchQuery, ignoreCase = true) ||
                (caseEntity.processNumber?.contains(searchQuery, ignoreCase = true) == true)
        
        val matchesFilter = when (selectedFilter) {
            "Active" -> caseEntity.status == "UPLOAD" || caseEntity.status == "ANALYSING"
            "Completed" -> caseEntity.status == "PDF_READY"
            "Archived" -> caseEntity.status == "SENT_TO_COURT"
            "Ativos" -> caseEntity.status == "UPLOAD" || caseEntity.status == "ANALYSING"
            "Concluídos" -> caseEntity.status == "PDF_READY"
            "Enviados" -> caseEntity.status == "SENT_TO_COURT"
            else -> true
        }
        matchesQuery && matchesFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewCaseDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(64.dp)
                    .testTag("new_case_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Case", modifier = Modifier.size(32.dp))
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "My Cases",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage and track the progress of your legal audits.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_cases_input"),
                    placeholder = { Text("Search cases...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    ),
                    singleLine = true
                )
            }

            // Filter Tabs
            LazyRow(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterOptions = listOf("All Cases", "Active", "Completed", "Archived")
                items(filterOptions) { label ->
                    val isSelected = selectedFilter == label || (selectedFilter == "All" && label == "All Cases") || (selectedFilter == "Todos" && label == "All Cases")
                    
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLowest,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { viewModel.setCaseFilter(if (label == "All Cases") "All" else label) }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cases List
            if (filteredCases.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No cases found",
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredCases) { caseEntity ->
                        MyCaseCardItem(
                            caseEntity = caseEntity,
                            onClick = {
                                viewModel.selectCase(caseEntity.id)
                                if (caseEntity.status == "PDF_READY" || caseEntity.status == "SENT_TO_COURT") {
                                    onNavigateToPetition(caseEntity.id)
                                } else {
                                    onNavigateToCalculation(caseEntity.id)
                                }
                            }
                        )
                    }
                    
                    item {
                        StartNewCaseCard(onClick = { showNewCaseDialog = true })
                    }
                }
            }
        }

        if (showNewCaseDialog) {
            NewCaseCreationDialog(
                onDismiss = { showNewCaseDialog = false },
                onCreate = { title, category, desc ->
                    viewModel.createNewCase(title, category, desc)
                    showNewCaseDialog = false
                }
            )
        }
    }
}

@Composable
fun MyCaseCardItem(
    caseEntity: CaseEntity,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("my_case_card_${caseEntity.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Row: Status Pill & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CaseStatusPill(status = caseEntity.status)
                Text(
                    text = caseEntity.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Title & Description
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = caseEntity.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = caseEntity.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            
            // Bottom Action Row
            CaseActionRow(caseEntity = caseEntity)
        }
    }
}

@Composable
fun CaseStatusPill(status: String) {
    val (icon, text, bgColor, textColor) = when (status) {
        "UPLOAD", "ANALYSING" -> listOf(
            Icons.Default.Sync,
            "Analyzing",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.primary
        )
        "PDF_READY" -> listOf(
            Icons.Default.TaskAlt,
            "PDF Ready",
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32) // Green colors
        )
        "SENT_TO_COURT" -> listOf(
            Icons.Default.Send,
            "Sent to Court",
            MaterialTheme.colorScheme.surfaceContainer,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        else -> listOf(
            Icons.Default.Info,
            "Unknown",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Surface(
        shape = CircleShape,
        color = bgColor as Color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector,
                contentDescription = null,
                tint = textColor as Color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text as String,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        }
    }
}

@Composable
fun CaseActionRow(caseEntity: CaseEntity) {
    when (caseEntity.status) {
        "UPLOAD", "ANALYSING" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceContainerLowest),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "DP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        "PDF_READY" -> {
            Button(
                onClick = { /* Handle Download */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download Report",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        "SENT_TO_COURT" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Process # ${caseEntity.processNumber ?: "0012345-67.2023"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StartNewCaseCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) // Use dashed effect if possible, but solid is fine for Compose easily
    ) {
        // Compose doesn't have an easy dashed border out of the box without Custom Modifier, using a subtle solid border
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Start a New Case",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upload your documents for a new automated legal audit.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCaseCreationDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Energia") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Energia", "Telefonia", "FGTS", "Trabalhista", "Voos", "Outros")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Iniciar Nova Auditoria", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Caso") },
                    placeholder = { Text("ex: Cobrança Abusiva Fatura Energia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_case_title_input"),
                    singleLine = true
                )

                Text("Categoria:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição dos Fatos") },
                    placeholder = { Text("Descreva resumidamente os valores cobrados...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_case_desc_input"),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, category, description)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_case_button")
            ) {
                Text("Criar e Anexar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

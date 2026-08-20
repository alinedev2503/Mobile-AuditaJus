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
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CaseEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AuditsTimelineSection
import com.example.ui.components.OfflineStatusBanner
import com.example.ui.theme.Pill
import com.example.ui.theme.caseStatusVisual
import com.example.util.rememberIsOnline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCasesScreen(
    viewModel: MainViewModel,
    onNavigateToCalculation: (Long) -> Unit,
    onNavigateToPetition: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOnline by rememberIsOnline()
    val cases by viewModel.cases.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedCaseFilter.collectAsState()
    var showNewCaseDialog by remember { mutableStateOf(false) }

    var selectedViewMode by remember { mutableStateOf(0) } // 0 = Lista de Casos, 1 = Linha do Tempo

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Meus Casos",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Acompanhe e audite processos no JEC",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Dynamic Offline Connection Status Warning Banner
                OfflineStatusBanner(isOnline = isOnline)
                
                Spacer(modifier = Modifier.height(4.dp))

                // View Mode Tabs: [📋 Lista de Casos] vs [⏱️ Linha do Tempo de Auditorias]
                PrimaryTabRow(
                    selectedTabIndex = selectedViewMode,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ) {
                    Tab(
                        selected = selectedViewMode == 0,
                        onClick = { selectedViewMode = 0 },
                        text = { Text("Lista de Casos (${cases.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.FolderShared, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedViewMode == 1,
                        onClick = { selectedViewMode = 1 },
                        text = { Text("Linha do Tempo", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Outlined.Timeline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                // Search Bar (visível na aba de lista de casos)
                if (selectedViewMode == 0) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_cases_input"),
                        placeholder = { Text("Pesquisar processos, partes ou categorias...") },
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
            }

            // Timeline View
            if (selectedViewMode == 1) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    item {
                        AuditsTimelineSection(
                            cases = cases,
                            onCaseClick = { caseEntity ->
                                viewModel.selectCase(caseEntity.id)
                                if (caseEntity.status == "PDF_READY" || caseEntity.status == "SENT_TO_COURT") {
                                    onNavigateToPetition(caseEntity.id)
                                } else {
                                    onNavigateToCalculation(caseEntity.id)
                                }
                            }
                        )
                    }
                }
            } else {
                // List View: Filter Tabs
                LazyRow(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf("Todos os Casos", "Ativos", "Concluídos", "Enviados")
                    items(filterOptions) { label ->
                        val isSelected = when (label) {
                            "Todos os Casos" -> selectedFilter == "All" || selectedFilter == "Todos" || selectedFilter == "All Cases"
                            "Ativos" -> selectedFilter == "Active" || selectedFilter == "Ativos"
                            "Concluídos" -> selectedFilter == "Completed" || selectedFilter == "Concluídos"
                            "Enviados" -> selectedFilter == "Archived" || selectedFilter == "Enviados"
                            else -> false
                        }
                        
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    val mapped = when (label) {
                                        "Todos os Casos" -> "All"
                                        "Ativos" -> "Active"
                                        "Concluídos" -> "Completed"
                                        "Enviados" -> "Archived"
                                        else -> label
                                    }
                                    viewModel.setCaseFilter(mapped)
                                }
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
                                text = "Nenhum caso encontrado",
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
                StatusPill(status = caseEntity.status)
                Text(
                    text = caseEntity.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Case Info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = caseEntity.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Categoria: ${caseEntity.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!caseEntity.processNumber.isNullOrEmpty()) {
                    Text(
                        text = "Processo: ${caseEntity.processNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Values & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Valor Liquidado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val total = caseEntity.subtotalUpdated + caseEntity.suggestedMoralDamages
                    Text(
                        text = "R$ %,.2f".format(total),
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
                        text = "Ver Detalhes",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val visual = caseStatusVisual(status)

    Surface(
        color = visual.containerColor,
        shape = Pill
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = visual.icon,
                contentDescription = null,
                tint = visual.contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = visual.label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = visual.contentColor
                )
            )
        }
    }
}

@Composable
fun StartNewCaseCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("start_new_case_dashed_card"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column {
                Text(
                    text = "Cadastrar Novo Processo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Faça a auditoria documental com a IA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
    var category by remember { mutableStateOf("Bancário") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Novo Processo de Auditoria", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Caso") },
                    placeholder = { Text("Ex: Revisional Financiamento Auto") },
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                val categories = listOf("Bancário", "Telecom", "Consumidor", "Energia / Luz", "Trabalhista", "Voos / Aéreo")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Resumo do Abuso / Descrição") },
                    placeholder = { Text("Cobrança indevida de seguro prestamista e taxa abusiva...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, category, desc)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Criar Processo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

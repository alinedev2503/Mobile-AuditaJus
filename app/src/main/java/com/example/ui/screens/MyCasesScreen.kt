package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
            "Ativos" -> caseEntity.status == "UPLOAD" || caseEntity.status == "ANALYSING"
            "Concluídos" -> caseEntity.status == "PDF_READY"
            "Enviados" -> caseEntity.status == "SENT_TO_COURT"
            else -> true
        }
        matchesQuery && matchesFilter
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Meus Casos",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_cases_input"),
                    placeholder = { Text("Buscar processos, faturas ou causas...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Limpar")
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                // Filter Pills Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        "Todos" to cases.size,
                        "Ativos" to cases.count { it.status == "UPLOAD" || it.status == "ANALYSING" },
                        "Concluídos" to cases.count { it.status == "PDF_READY" },
                        "Enviados" to cases.count { it.status == "SENT_TO_COURT" }
                    )

                    items(filterOptions) { (label, count) ->
                        val isSelected = selectedFilter == label || (selectedFilter == "All" && label == "Todos")
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCaseFilter(if (label == "Todos") "All" else label) },
                            label = { Text("$label ($count)") },
                            modifier = Modifier.testTag("filter_chip_$label"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewCaseDialog = true },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo Caso") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.testTag("new_case_fab")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (filteredCases.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
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
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("my_case_card_${caseEntity.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Text(
                    text = caseEntity.category,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                StatusBadge(status = caseEntity.status)
            }

            Text(
                text = caseEntity.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = caseEntity.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data: ${caseEntity.date}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Text(
                    text = "Dano Est.: R$ %.2f".format(caseEntity.subtotalUpdated + caseEntity.suggestedMoralDamages),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
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
    var category by remember { mutableStateOf("Energia") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Energia", "Telefonia", "FGTS", "Trabalhista", "Voos", "Outros")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Iniciar Nova Auditoria", fontWeight = FontWeight.Bold)
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

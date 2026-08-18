package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.HearingDeadlineEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDeadlinesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.hearingsDeadlines.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agenda e Prazos do JEC",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_calendar_event_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar compromisso")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.testTag("calendar_fab")
            ) {
                Icon(imageVector = Icons.Default.Event, contentDescription = null)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Calendar Header Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Próximos Compromissos",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "${items.count { !it.isCompleted }} eventos pendentes no calendário do JEC",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Audiências e Prazos Críticos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (items.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum compromisso agendado.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            } else {
                items(items) { item ->
                    CalendarEventCardItem(
                        item = item,
                        onToggle = { viewModel.toggleHearingDeadline(item) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddHearingDeadlineDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, date, time, type, notes ->
                    viewModel.addHearingDeadline(com.example.data.db.HearingDeadlineEntity(caseId = 1L, title = title, dateString = date, timeString = time, type = type, locationOrNotes = notes))
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun CalendarEventCardItem(
    item: HearingDeadlineEntity,
    onToggle: () -> Unit
) {
    val isHearing = item.type == "HEARING"
    val accentColor = if (isHearing) MaterialTheme.colorScheme.primary else Color(0xFFBA1A1A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_item_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isHearing) "Audiência" else "Prazo Crítico",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        )
                    }

                    Text(
                        text = "${item.dateString} às ${item.timeString}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (item.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                )

                if (item.locationOrNotes.isNotBlank()) {
                    Text(
                        text = item.locationOrNotes,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}

@Composable
fun AddHearingDeadlineDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("25/11/2026") }
    var time by remember { mutableStateOf("14:30") }
    var type by remember { mutableStateOf("HEARING") }
    var notes by remember { mutableStateOf("Juizado Especial Cível") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agendar compromisso no JEC", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Evento") },
                    placeholder = { Text("ex: Audiência de Conciliação") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == "HEARING",
                        onClick = { type = "HEARING" },
                        label = { Text("Audiência") }
                    )
                    FilterChip(
                        selected = type == "DEADLINE",
                        onClick = { type = "DEADLINE" },
                        label = { Text("Prazo") }
                    )
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data (DD/MM/AAAA)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Horário (HH:MM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Local / Observações") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, date, time, type, notes)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Agendar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

package com.example.ui.screens

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
import com.example.ui.MainViewModel

data class LegalGuideItem(
    val id: String,
    val title: String,
    val category: String,
    val snippet: String,
    val readTimeMinutes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalGuidesScreen(
    viewModel: MainViewModel,
    onOpenAiAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedFilter by viewModel.guideFilter.collectAsState()
    var searchStr by remember { mutableStateOf("") }
    var selectedGuideForDetail by remember { mutableStateOf<LegalGuideItem?>(null) }

    val allGuides = remember {
        listOf(
            LegalGuideItem(
                id = "1",
                title = "Seus Direitos na Luz: Cobranças Abusivas e Cortes",
                category = "Consumidor",
                snippet = "Saiba o que fazer em caso de cobrança indevida na conta de energia, bandeiras tarifárias irregulares, corte sem aviso prévio ou queima de aparelhos.",
                readTimeMinutes = 5
            ),
            LegalGuideItem(
                id = "2",
                title = "JEC 101: Como funciona o Juizado Especial Cível?",
                category = "Consumidor",
                snippet = "Passo a passo completo para dar entrada em pequenas causas (até 20 salários mínimos sem advogado), prazos e o que acontece na audiência de conciliação.",
                readTimeMinutes = 7
            ),
            LegalGuideItem(
                id = "3",
                title = "Provas em Conversas de WhatsApp e E-mail",
                category = "Consumidor",
                snippet = "Aprenda a estruturar prints e arquivos digitais de forma válida e aceita pelos juízes dos Juizados Especiais Cíveis.",
                readTimeMinutes = 4
            ),
            LegalGuideItem(
                id = "4",
                title = "Direitos do Trabalhista no JEC / FGTS",
                category = "Trabalhista",
                snippet = "Como solicitar o recálculo do FGTS e correção inflacionária sem taxas abusivas de intermediários.",
                readTimeMinutes = 6
            ),
            LegalGuideItem(
                id = "5",
                title = "Direitos do Passageiro: Atrasos de Voo e Extravio de Bagagem",
                category = "Voos",
                snippet = "Resolução 400 da ANAC: Assistência material obrigatória (alimentação, hospedagem e transporte) e indenização por danos morais.",
                readTimeMinutes = 5
            )
        )
    }

    val filteredGuides = allGuides.filter { item ->
        val matchesCategory = selectedFilter == "Todos" || item.category.equals(selectedFilter, ignoreCase = true)
        val matchesQuery = searchStr.isBlank() || item.title.contains(searchStr, ignoreCase = true) || item.snippet.contains(searchStr, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Guias Jurídicos",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    IconButton(
                        onClick = onOpenAiAssistant,
                        modifier = Modifier.testTag("guides_ai_assistant_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = "Tirar Dúvida com IA",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                OutlinedTextField(
                    value = searchStr,
                    onValueChange = { searchStr = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_guides_input"),
                    placeholder = { Text("Pesquisar direitos, regras ou dúvidas...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categories = listOf("Todos", "Consumidor", "Trabalhista", "Voos")
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedFilter == cat,
                            onClick = { viewModel.setGuideFilter(cat) },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("guide_filter_$cat")
                        )
                    }
                }
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
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Featured Guide Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedGuideForDetail = allGuides.first() }
                        .testTag("featured_guide_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Destaque da Semana",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Text(
                            text = "Seus Direitos na Luz: Cobranças Abusivas e Cortes",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        Text(
                            text = "Saiba o que fazer em caso de cobrança indevida na conta de energia, bandeiras tarifárias irregulares ou queima de aparelhos.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        Button(
                            onClick = { selectedGuideForDetail = allGuides.first() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Ler Guia Completo", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Todos os Artigos e Tutoriais",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            items(filteredGuides) { guide ->
                GuideCardItem(
                    guide = guide,
                    onClick = { selectedGuideForDetail = guide }
                )
            }
        }

        selectedGuideForDetail?.let { detailGuide ->
            GuideDetailDialog(
                guide = detailGuide,
                onDismiss = { selectedGuideForDetail = null }
            )
        }
    }
}

@Composable
fun GuideCardItem(
    guide: LegalGuideItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("guide_item_${guide.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guide.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${guide.readTimeMinutes} min de leitura",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Text(
                text = guide.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = guide.snippet,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
fun GuideDetailDialog(
    guide: LegalGuideItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(guide.title, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Categoria: ${guide.category}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }

                Text(
                    text = guide.snippet,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "No Juizado Especial Cível (JEC), ações de até 20 salários mínimos são isentas de custas iniciais e não exigem contratação prévia de advogado. O artigo 42 do Código de Defesa do Consumidor garante que qualquer quantia cobrada injustamente deve ser restituída em dobro.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}

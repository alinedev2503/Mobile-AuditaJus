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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.warningColors

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
    val favoriteGuidesList by viewModel.favoriteGuides.collectAsState()
    val favoriteIds = remember(favoriteGuidesList) { favoriteGuidesList.map { it.guideId }.toSet() }

    var searchStr by remember { mutableStateOf("") }
    var selectedGuideForDetail by remember { mutableStateOf<LegalGuideItem?>(null) }

    val allGuides = remember {
        listOf(
            LegalGuideItem(
                id = "rep_dobro",
                title = "Repetição em Dobro no CDC: Como Exigir 2x o Valor Cobrado",
                category = "Repetição em Dobro",
                snippet = "Entenda a tese fixada pelo STJ no EAREsp 676.608/RJ: a devolução em dobro independe de má-fé, bastando conduta contrária à boa-fé objetiva.",
                readTimeMinutes = 5
            ),
            LegalGuideItem(
                id = "bancario",
                title = "Revisional de Empréstimos: Juros Abusivos e Taxa Média BACEN",
                category = "Bancário",
                snippet = "Como comprovar a cobrança de juros remuneratórios superiores à média de mercado apurada pelo Banco Central e expurgar a abusividade.",
                readTimeMinutes = 6
            ),
            LegalGuideItem(
                id = "telecom",
                title = "Telecom & SVA: Cancelamento e Restituição de Serviços Não Solicitados",
                category = "Telecom",
                snippet = "Pacotes de dados avulsos, antivírus, bancas de revista e seguros inseridos sem consentimento na conta telefônica dão direito à dobra.",
                readTimeMinutes = 4
            ),
            LegalGuideItem(
                id = "luz_cortes",
                title = "Seus Direitos na Luz: Cobranças Abusivas e Cortes",
                category = "Consumidor",
                snippet = "Saiba o que fazer em caso de cobrança indevida na conta de energia, bandeiras tarifárias irregulares, corte sem aviso prévio ou queima de aparelhos.",
                readTimeMinutes = 5
            ),
            LegalGuideItem(
                id = "jec_101",
                title = "JEC 101: Como funciona o Juizado Especial Cível?",
                category = "Consumidor",
                snippet = "Passo a passo completo para dar entrada em pequenas causas (até 20 salários mínimos sem advogado), prazos e o que acontece na audiência de conciliação.",
                readTimeMinutes = 7
            ),
            LegalGuideItem(
                id = "provas_whatsapp",
                title = "Provas em Conversas de WhatsApp e E-mail",
                category = "Consumidor",
                snippet = "Aprenda a estruturar prints e arquivos digitais de forma válida e aceita pelos juízes dos Juizados Especiais Cíveis.",
                readTimeMinutes = 4
            ),
            LegalGuideItem(
                id = "fgts_trabalhista",
                title = "Direitos do Trabalhador no JEC / FGTS",
                category = "Trabalhista",
                snippet = "Como solicitar o recálculo do FGTS e correção inflacionária sem taxas abusivas de intermediários.",
                readTimeMinutes = 6
            ),
            LegalGuideItem(
                id = "voos_anac",
                title = "Direitos do Passageiro: Atrasos de Voo e Extravio de Bagagem",
                category = "Voos",
                snippet = "Resolução 400 da ANAC: Assistência material obrigatória (alimentação, hospedagem e transporte) e indenização por danos morais.",
                readTimeMinutes = 5
            )
        )
    }

    val filteredGuides = allGuides.filter { item ->
        val matchesCategory = when (selectedFilter) {
            "Todos" -> true
            "⭐ Favoritos" -> favoriteIds.contains(item.id)
            else -> item.category.equals(selectedFilter, ignoreCase = true)
        }
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
                    Column {
                        Text(
                            text = "Guias Jurídicos & Leis",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Artigos do CDC, STJ e Jurisprudência Comentada",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

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
                    placeholder = { Text("Pesquisar direitos, leis, regras ou dúvidas...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categories = listOf("Todos", "⭐ Favoritos", "Repetição em Dobro", "Bancário", "Telecom", "Consumidor", "Trabalhista", "Voos")
                    items(categories) { cat ->
                        val isSelected = selectedFilter == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setGuideFilter(cat) },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                                    if (cat == "⭐ Favoritos" && favoriteIds.isNotEmpty()) {
                                        Surface(
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "${favoriteIds.size}",
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.testTag("guide_filter_${cat.replace(" ", "_")}"),
                            shape = MaterialTheme.shapes.large,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
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
            // Favoritos Horizontal Carousel (Quando existirem favoritos e filtro for "Todos")
            if (selectedFilter == "Todos" && favoriteGuidesList.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = warningColors().content,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Seus Guias & Leis Salvas",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            TextButton(onClick = { viewModel.setGuideFilter("⭐ Favoritos") }) {
                                Text("Ver Todos (${favoriteGuidesList.size})", fontSize = 12.sp)
                            }
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(favoriteGuidesList) { fav ->
                                val matchingGuide = allGuides.find { it.id == fav.guideId } ?: LegalGuideItem(
                                    id = fav.guideId,
                                    title = fav.title,
                                    category = fav.category,
                                    snippet = fav.snippet,
                                    readTimeMinutes = fav.readTimeMinutes
                                )
                                FavoriteGuideMiniCard(
                                    guide = matchingGuide,
                                    onClick = { selectedGuideForDetail = matchingGuide },
                                    onUnfavorite = {
                                        viewModel.removeFavoriteGuide(fav.guideId)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Featured Guide Card (apenas na aba "Todos" e sem pesquisa)
            if (selectedFilter == "Todos" && searchStr.isBlank()) {
                item {
                    val featured = allGuides.first()
                    val isFav = favoriteIds.contains(featured.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGuideForDetail = featured }
                            .testTag("featured_guide_card"),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        text = "Tese em Destaque",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.toggleFavoriteGuide(
                                            guideId = featured.id,
                                            title = featured.title,
                                            category = featured.category,
                                            snippet = featured.snippet,
                                            readTimeMinutes = featured.readTimeMinutes,
                                            isCurrentlyFavorite = isFav
                                        )
                                    },
                                    modifier = Modifier.testTag("favorite_featured_button")
                                ) {
                                    Icon(
                                        imageVector = if (isFav) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = if (isFav) "Desfavoritar" else "Favoritar",
                                        tint = if (isFav) warningColors().content else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Text(
                                text = featured.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )

                            Text(
                                text = featured.snippet,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            )

                            Button(
                                onClick = { selectedGuideForDetail = featured },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Ler Tese Completa", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = if (selectedFilter == "⭐ Favoritos") "Seus Artigos e Teses Salvas" else "Artigos e Legislação Aplicada",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            if (filteredGuides.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = if (selectedFilter == "⭐ Favoritos") "⭐" else "🔍", fontSize = 32.sp)
                            Text(
                                text = if (selectedFilter == "⭐ Favoritos") "Nenhum guia favoritado ainda" else "Nenhum resultado encontrado",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (selectedFilter == "⭐ Favoritos") "Toque no ícone de marcador nos cards para salvar leis para acesso rápido." else "Tente buscar com outros termos como 'CDC', 'Juros' ou 'STJ'.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredGuides) { guide ->
                    val isFav = favoriteIds.contains(guide.id)
                    GuideCardItem(
                        guide = guide,
                        isFavorite = isFav,
                        onToggleFavorite = {
                            viewModel.toggleFavoriteGuide(
                                guideId = guide.id,
                                title = guide.title,
                                category = guide.category,
                                snippet = guide.snippet,
                                readTimeMinutes = guide.readTimeMinutes,
                                isCurrentlyFavorite = isFav
                            )
                        },
                        onClick = { selectedGuideForDetail = guide }
                    )
                }
            }
        }

        selectedGuideForDetail?.let { detailGuide ->
            val isFav = favoriteIds.contains(detailGuide.id)
            GuideDetailDialog(
                guide = detailGuide,
                isFavorite = isFav,
                onToggleFavorite = {
                    viewModel.toggleFavoriteGuide(
                        guideId = detailGuide.id,
                        title = detailGuide.title,
                        category = detailGuide.category,
                        snippet = detailGuide.snippet,
                        readTimeMinutes = detailGuide.readTimeMinutes,
                        isCurrentlyFavorite = isFav
                    )
                },
                onDismiss = { selectedGuideForDetail = null }
            )
        }
    }
}

@Composable
fun FavoriteGuideMiniCard(
    guide: LegalGuideItem,
    onClick: () -> Unit,
    onUnfavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("saved_guide_mini_card_${guide.id}"),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = guide.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                IconButton(
                    onClick = onUnfavorite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Remover dos favoritos",
                        tint = warningColors().content,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = guide.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp
                ),
                maxLines = 2
            )

            Text(
                text = "${guide.readTimeMinutes} min de leitura",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun GuideCardItem(
    guide: LegalGuideItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("guide_item_${guide.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = guide.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "${guide.readTimeMinutes} min de leitura",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp).testTag("guide_bookmark_button_${guide.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isFavorite) "Remover favorito" else "Salvar nos favoritos",
                        tint = if (isFavorite) warningColors().content else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guide.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Favoritar",
                        tint = if (isFavorite) warningColors().content else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Categoria: ${guide.category} • Leitura de ${guide.readTimeMinutes} min",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }

                Text(
                    text = guide.snippet,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "💡 Fundamentação no JEC:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "No Juizado Especial Cível (Lei 9.099/95), causas de até 20 salários mínimos são isentas de custas iniciais e não exigem contratação de advogado. O artigo 42 do CDC assegura devolução em dobro do valor pago indevidamente, corrigido monetariamente pelo INPC e juros de 1% a.m.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

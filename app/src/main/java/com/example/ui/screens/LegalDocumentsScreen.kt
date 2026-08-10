package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var hasAccepted by remember { mutableStateOf(true) }

    val userSettings by viewModel.userSettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Documentos Legais & LGPD", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("legal_docs_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Termos de Uso") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Privacidade (LGPD)") }
                )
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedTab == 0) {
                        item {
                            Text(
                                text = "TERMOS E CONDIÇÕES DE USO - CONTADOR JURÍDICO PRO",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        item {
                            Text(
                                text = "1. OBJETO E FINALIDADE\n" +
                                        "O aplicativo Contador Jurídico Pro é uma ferramenta de auxílio na auditoria e cálculo de pequenos débitos judiciais com base na tabela do Juizado Especial Cível (JEC). O aplicativo utiliza tecnologia de Inteligência Artificial (Gemini API) para extração automatizada de dados e organização formal de minutas de petições iniciais.\n\n" +
                                        "2. NATUREZA DO SERVIÇO\n" +
                                        "O Contador Jurídico Pro é um aplicativo utilitário e informativo. As minutas geradas e estimativas de danos morais constituem sugestões baseadas na jurisprudência dos Juizados Especiais, cabendo ao usuário e/ou seu procurador a conferência final antes do protocolo judicial.\n\n" +
                                        "3. PROPRIEDADE INTELECTUAL E DADOS\n" +
                                        "Todos os documentos anexados são criptografados localmente e mantidos sob total sigilo, respeitando o segredo de justiça e as diretrizes da Ordem dos Advogados do Brasil (OAB) e da LGPD.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "POLÍTICA DE PRIVACIDADE E PROTEÇÃO DE DADOS (LGPD)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        item {
                            Text(
                                text = "1. TRATAMENTO DE DADOS PESSOAIS (LEI 13.709/2018)\n" +
                                        "Coletamos e processamos única e exclusivamente os dados estritamente necessários para a elaboração do cálculo e petição inicial, como nome, CPF, faturas de cobrança e histórico do caso.\n\n" +
                                        "2. ARMAZENAMENTO SEGURO\n" +
                                        "Os arquivos e fotos de contratos e extratos são armazenados em banco de dados local seguro no dispositivo (Room Database Criptografado), não sendo compartilhados com terceiros comerciais sem o seu consentimento explícito.\n\n" +
                                        "3. DIREITOS DO TITULAR\n" +
                                        "A qualquer momento o usuário pode solicitar o apagamento total dos seus casos e histórico de auditorias através do menu Ajustes.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = hasAccepted,
                    onCheckedChange = {
                        hasAccepted = it
                        viewModel.setAcceptedTerms(it)
                    },
                    modifier = Modifier.testTag("accept_terms_checkbox")
                )
                Text(
                    text = "Li e concordo com os Termos de Uso e Política de Privacidade",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
            }

            Button(
                onClick = onNavigateBack,
                enabled = hasAccepted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("accept_terms_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aceitar e Continuar", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Shield
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
    val isSystemDark = isSystemInDarkTheme()
    val isDark = userSettings.isDarkMode ?: isSystemDark

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(6.dp).size(20.dp)
                            )
                        }
                        Text(
                            text = "AuditaJus",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("legal_docs_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setDarkMode(!isDark) },
                        modifier = Modifier.testTag("toggle_dark_mode_button")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Alternar Modo Claro/Escuro",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                hasAccepted = !hasAccepted
                                viewModel.setAcceptedTerms(hasAccepted)
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = hasAccepted,
                            onCheckedChange = {
                                hasAccepted = it
                                viewModel.setAcceptedTerms(it)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("accept_terms_checkbox")
                        )
                        Text(
                            text = "Li e concordo com os Termos de Uso e a Política de Privacidade.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = onNavigateBack,
                        enabled = hasAccepted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("accept_terms_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aceitar e Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
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
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Documentos Legais",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Última atualização: 24 de Outubro de 2023",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Custom Segmented Switcher / Pill Tabs
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LegalTabButton(
                        title = "Termos de Uso",
                        icon = Icons.Outlined.Description,
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("tab_terms_of_use")
                    )
                    LegalTabButton(
                        title = "Privacidade",
                        icon = Icons.Outlined.Shield,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("tab_privacy_policy")
                    )
                }
            }

            // Document Content Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (selectedTab == 0) {
                        item {
                            Text(
                                text = "Termos e Condições de Uso",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        }

                        item {
                            LegalSection(
                                number = "1. ACEITAÇÃO DOS TERMOS",
                                body = "Ao acessar e utilizar a plataforma AuditaJus, você concorda expressamente com estes termos. Se você não concorda com qualquer parte destes termos, não deverá utilizar nossos serviços. O uso contínuo constitui aceitação de quaisquer alterações."
                            )
                        }

                        item {
                            LegalSectionWithBullets(
                                number = "2. SERVIÇOS PRESTADOS",
                                intro = "A plataforma oferece ferramentas de auditoria e gestão de documentos jurídicos. Nossos serviços incluem:",
                                bullets = listOf(
                                    "Análise automatizada de conformidade contratual e extração de evidências com IA Gemini.",
                                    "Cálculo atualizado de juros legais, correção monetária (INPC) e estimativa de danos morais.",
                                    "Armazenamento seguro de registros de auditoria e geração de minutas para o JEC em PDF."
                                ),
                                warningNote = "Aviso: Nossas ferramentas auxiliam profissionais e cidadãos, mas não substituem o aconselhamento ou representação jurídica formal por advogado ou defensor público."
                            )
                        }

                        item {
                            LegalSection(
                                number = "3. RESPONSABILIDADES DO USUÁRIO",
                                body = "Você é o único responsável pela veracidade e legalidade dos dados inseridos. O uso indevido da plataforma para fins ilícitos, engenharia reversa ou sobrecarga intencional de nossos servidores resultará no banimento imediato e ações legais cabíveis."
                            )
                        }

                        item {
                            LegalSection(
                                number = "4. PROPRIEDADE INTELECTUAL",
                                body = "Todos os algoritmos, interfaces, marcas e conteúdos gerados pela plataforma (exceto seus próprios documentos) são propriedade exclusiva do AuditaJus, protegidos por leis de direitos autorais e propriedade industrial."
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "Política de Privacidade (LGPD)",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        }

                        item {
                            LegalSection(
                                number = "1. COLETA DE DADOS",
                                body = "Coletamos apenas as informações estritamente necessárias para a prestação dos nossos serviços de auditoria, incluindo dados cadastrais (nome, registro OAB, e-mail) e metadados dos documentos processados pela plataforma."
                            )
                        }

                        item {
                            LegalSectionWithBullets(
                                number = "2. USO E TRATAMENTO (LGPD)",
                                intro = "Em conformidade com a Lei Geral de Proteção de Dados (Lei nº 13.709/2018), seus dados pessoais são utilizados exclusivamente para:",
                                bullets = listOf(
                                    "Autenticação e segurança da sua conta na plataforma.",
                                    "Processamento de relatórios e petições solicitados por você.",
                                    "Melhoria contínua dos modelos de análise jurídica de forma totalmente anonimizada."
                                ),
                                warningNote = null
                            )
                        }

                        item {
                            LegalSection(
                                number = "3. SEGURANÇA DA INFORMAÇÃO",
                                body = "Adotamos criptografia de ponta a ponta (AES-256) para dados em repouso no dispositivo e conexões seguras TLS para chamadas de API. Seus arquivos de provas não são vendidos nem compartilhados com terceiros comerciais."
                            )
                        }

                        item {
                            LegalSection(
                                number = "4. DIREITOS DO TITULAR",
                                body = "Em conformidade com o Art. 18 da LGPD, o usuário possui total direito de consultar, retificar ou excluir permanentemente seu histórico de auditorias e conta a qualquer momento pelo menu Configurações."
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LegalTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null,
        shadowElevation = if (isSelected) 3.dp else 0.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun LegalSection(
    number: String,
    body: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = number,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun LegalSectionWithBullets(
    number: String,
    intro: String,
    bullets: List<String>,
    warningNote: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = number,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = intro,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            bullets.forEach { bullet ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
        if (warningNote != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = warningNote,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

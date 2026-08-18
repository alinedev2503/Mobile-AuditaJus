package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
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
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var hasAccepted by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Contador Jurídico Pro",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { hasAccepted = !hasAccepted }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = hasAccepted,
                            onCheckedChange = { hasAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.size(24.dp).padding(top = 2.dp)
                        )
                        Text(
                            text = "Li e concordo com os Termos de Uso e a Política de Privacidade.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    val buttonContainer = when {
                        isSuccess -> Color(0xFF10B981) // Success Green
                        hasAccepted -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val buttonContent = when {
                        isSuccess -> Color.White
                        hasAccepted -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                    
                    Button(
                        onClick = {
                            if (!isProcessing && !isSuccess) {
                                isProcessing = true
                                coroutineScope.launch {
                                    delay(1000)
                                    isProcessing = false
                                    isSuccess = true
                                    viewModel.setAcceptedTerms(true)
                                    delay(500)
                                    onNavigateBack()
                                }
                            }
                        },
                        enabled = hasAccepted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonContainer,
                            contentColor = buttonContent,
                            disabledContainerColor = buttonContainer,
                            disabledContentColor = buttonContent
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp),
                        shape = CircleShape
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = buttonContent,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processando...", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        } else if (isSuccess) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aceito", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        } else {
                            Text("Aceitar e Continuar", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Documentos Legais",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Última atualização: 24 de Outubro de 2023",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        TabButton(
                            text = "Termos de Uso",
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        TabButton(
                            text = "Privacidade",
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                    }
                }
            }
            
            // Content Card
            val scrollState = rememberScrollState()
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp)
                    ) {
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                            },
                            label = "TabContent"
                        ) { tab ->
                            if (tab == 0) {
                                TermosDeUsoContent()
                            } else {
                                PrivacidadeContent()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val shadow = if (isSelected) 1.dp else 0.dp
    
    Surface(
        shape = CircleShape,
        color = bgColor,
        contentColor = contentColor,
        shadowElevation = shadow,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun TermosDeUsoContent() {
    Column {
        Text(
            text = "Termos e Condições de Uso",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "1. ACEITAÇÃO DOS TERMOS",
            body = "Ao acessar e utilizar a plataforma Contador Jurídico Pro, você concorda expressamente com estes termos. Se você não concorda com qualquer parte destes termos, não deverá utilizar nossos serviços. O uso contínuo constitui aceitação de quaisquer alterações."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "2. SERVIÇOS PRESTADOS",
            body = "A plataforma oferece ferramentas de auditoria e gestão de documentos jurídicos. Nossos serviços incluem:\n\n" +
                   "• Análise automatizada de conformidade contratual.\n" +
                   "• Armazenamento seguro de evidências de auditoria.\n" +
                   "• Geração de relatórios de diligência.",
            warning = "Aviso: Nossas ferramentas auxiliam profissionais, mas não substituem o aconselhamento jurídico formal."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "3. RESPONSABILIDADES DO USUÁRIO",
            body = "Você é o único responsável pela veracidade e legalidade dos dados inseridos. O uso indevido da plataforma para fins ilícitos, engenharia reversa ou sobrecarga intencional de nossos servidores resultará no banimento imediato e ações legais cabíveis."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "4. PROPRIEDADE INTELECTUAL",
            body = "Todos os algoritmos, interfaces, marcas e conteúdos gerados pela plataforma (exceto seus próprios documentos) são propriedade exclusiva do Contador Jurídico Pro, protegidos por leis de direitos autorais e propriedade industrial."
        )
    }
}

@Composable
fun PrivacidadeContent() {
    Column {
        Text(
            text = "Política de Privacidade",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "1. COLETA DE DADOS",
            body = "Coletamos apenas as informações estritamente necessárias para a prestação dos nossos serviços de auditoria, incluindo dados cadastrais (nome, OAB, e-mail) e os metadados dos documentos processados pela plataforma."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "2. USO E TRATAMENTO (LGPD)",
            body = "Em conformidade com a Lei Geral de Proteção de Dados (LGPD), seus dados são utilizados exclusivamente para:\n\n" +
                   "• Autenticação e segurança da sua conta.\n" +
                   "• Processamento de relatórios solicitados por você.\n" +
                   "• Melhoria contínua dos algoritmos (de forma anonimizada)."
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LegalSection(
            title = "3. SEGURANÇA DA INFORMAÇÃO",
            body = "Adotamos criptografia de ponta a ponta (AES-256) para dados em repouso e TLS 1.3 para dados em trânsito. Nossos servidores estão localizados em data centers com certificação ISO 27001."
        )
    }
}

@Composable
fun LegalSection(title: String, body: String, warning: String? = null) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 28.sp,
                fontFamily = FontFamily.Serif
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (warning != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = warning,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp,
                    fontFamily = FontFamily.Serif
                ),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

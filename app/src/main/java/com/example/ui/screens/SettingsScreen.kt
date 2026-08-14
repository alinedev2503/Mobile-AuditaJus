package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateToTerms: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajustes e Configurações",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
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
            // Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_profile_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        ) {
                            AsyncImage(
                                model = userSettings.avatarUrl,
                                contentDescription = "Perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userSettings.userName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = userSettings.userEmail,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                }
            }

            // Section 1: Conta
            item {
                Text(
                    text = "Minha Conta",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Alterar Senha") },
                            leadingContent = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.testTag("change_password_item")
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ListItem(
                            headlineContent = { Text("Dados Pessoais & Documentos") },
                            leadingContent = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                        )
                    }
                }
            }

            // Section 2: Notificações & Segurança
            item {
                Text(
                    text = "Preferências e Segurança",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Notificações Push") },
                            supportingContent = { Text("Alertas de alteração de status e prazos no JEC") },
                            leadingContent = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = userSettings.isPushEnabled,
                                    onCheckedChange = { viewModel.setPushEnabled(it) },
                                    modifier = Modifier.testTag("push_switch")
                                )
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ListItem(
                            headlineContent = { Text("Login Biométrico / Fingerprint") },
                            supportingContent = { Text("Desbloquear app com Digital ou FaceID") },
                            leadingContent = { Icon(Icons.Outlined.Fingerprint, contentDescription = null) },
                            trailingContent = {
                                Switch(
                                    checked = userSettings.isBiometricEnabled,
                                    onCheckedChange = { viewModel.setBiometricEnabled(it) },
                                    modifier = Modifier.testTag("biometric_switch")
                                )
                            }
                        )
                    }
                }
            }

            // Section 3: Documentos Legais e Sobre
            item {
                Text(
                    text = "Legal e Suporte",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Termos de Uso e Privacidade (LGPD)") },
                            leadingContent = { Icon(Icons.Outlined.Policy, contentDescription = null) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.testTag("terms_item").clickable { onNavigateToTerms() }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ListItem(
                            headlineContent = { Text("Suporte Jurídico e Dúvidas") },
                            leadingContent = { Icon(Icons.Outlined.HelpOutline, contentDescription = null) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ListItem(
                            headlineContent = { Text("Versão do Aplicativo") },
                            supportingContent = { Text("AuditaJus v1.0.0") },
                            leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) }
                        )
                    }
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("logout_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sair da Conta", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showEditProfileDialog) {
            EditProfileDialog(
                currentName = userSettings.userName,
                currentEmail = userSettings.userEmail,
                onDismiss = { showEditProfileDialog = false },
                onSave = { newName, newEmail ->
                    viewModel.setLoggedIn(true, newName, newEmail)
                    showEditProfileDialog = false
                }
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, email) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

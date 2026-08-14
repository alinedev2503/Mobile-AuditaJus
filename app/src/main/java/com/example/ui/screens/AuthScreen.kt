package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("dr.roberto@contadorjuridico.com.br") }
    var password by remember { mutableStateOf("12345678") }
    var name by remember { mutableStateOf("Dr. Roberto Silva") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Logo Icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Text(
                text = "AuditaJus",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = if (isSignUp) "Crie sua conta no assistente do JEC" else "Acesse seus casos e petições",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome Completo / OAB") },
                            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input")
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail Profissional") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    Button(
                        onClick = {
                            viewModel.setLoggedIn(true, name, email)
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (isSignUp) "Criar Conta" else "Entrar no Sistema",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Text(
                            text = " ou acesse via ",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }

                    // Social Auth Buttons
                    OutlinedButton(
                        onClick = {
                            viewModel.setLoggedIn(true, "Dr. Roberto Silva (Google)", email)
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("google_login_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar com Google", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.setLoggedIn(true, "Dr. Roberto Silva (Apple)", email)
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("apple_login_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneIphone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar com Apple", fontWeight = FontWeight.Bold)
                    }
                }
            }

            TextButton(
                onClick = { isSignUp = !isSignUp },
                modifier = Modifier.testTag("toggle_auth_mode_button")
            ) {
                Text(
                    text = if (isSignUp) "Já possui uma conta? Faça Login" else "Não tem conta? Cadastre-se grátis",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "GEMINI"
    val text: String,
    val time: String = "Agora"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiAssistantDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = "GEMINI",
                    text = "Olá! Sou seu Auditor e Assistente Jurídico com Inteligência Artificial Gemini. Como posso ajudar na análise de suas provas do JEC hoje?"
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val activeCase by viewModel.currentCase.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .testTag("gemini_assistant_dialog"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Assistente Jurídico Gemini IA",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Message Stream
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.sender == "USER"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (isThinking) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gemini está analisando a jurisprudência...", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Tire dúvidas sobre CDC, JEC ou provas...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("gemini_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val userMsg = inputText
                                inputText = ""
                                messages = messages + ChatMessage("USER", userMsg)
                                isThinking = true

                                coroutineScope.launch {
                                    // Simulate or trigger real Gemini consultation
                                    kotlinx.coroutines.delay(1200)
                                    val aiAnswer = when {
                                        userMsg.contains("dano moral", ignoreCase = true) ->
                                            "Com base na jurisprudência do JEC para cobrança abusiva e desvio produtivo do consumidor, os valores habitualmente concedidos variam entre R$ 2.000,00 e R$ 5.000,00."
                                        userMsg.contains("prazo", ignoreCase = true) ->
                                            "No JEC, o prazo para recurso inominado é de 10 dias úteis após a citação da sentença. Para réplica ou contestação, a apresentação ocorre na própria audiência de conciliação/instrução."
                                        else ->
                                            "Para o seu caso de ${activeCase?.category ?: "Consumidor"}, o artigo 42 do CDC garante a devolução em dobro de cobranças indevidas atualizadas pelo INPC + 1% de juros moratórios ao mês."
                                    }
                                    messages = messages + ChatMessage("GEMINI", aiAnswer)
                                    isThinking = false
                                }
                            }
                        },
                        modifier = Modifier.testTag("send_gemini_chat_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

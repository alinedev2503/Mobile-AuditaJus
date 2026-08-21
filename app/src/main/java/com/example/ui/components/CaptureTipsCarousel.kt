package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Pill
import com.example.ui.theme.filedColors
import com.example.ui.theme.infoColors
import com.example.ui.theme.successColors
import com.example.ui.theme.warningColors

data class CaptureTip(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val scoreBoost: String,
    val tag: String,
    val tagColor: Color,
    val doTip: String,
    val dontTip: String
)

@Composable
fun CaptureTipsCarousel(
    onTipClick: (CaptureTip) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tips = listOf(
        CaptureTip(
            id = "light",
            title = "Iluminação Uniforme & Sem Reflexos",
            description = "Evite sombras projetadas pelo celular ou reflexos de lâmpadas diretas sobre o papel ou tela do computador.",
            icon = Icons.Outlined.WbSunny,
            scoreBoost = "+25% Confiança",
            tag = "ILUMINAÇÃO",
            tagColor = warningColors().content,
            doTip = "Fotografe próximo a uma janela com luz natural ou luz difusa.",
            dontTip = "Não use o flash direto muito próximo para não 'cegar' os valores numéricos."
        ),
        CaptureTip(
            id = "focus",
            title = "Foco em Linhas de Tarifas e Valores",
            description = "Toque na tela sobre a tabela de encargos, juros e descrição de taxas para garantir nitidez dos centavos.",
            icon = Icons.Outlined.CenterFocusStrong,
            scoreBoost = "+30% Precisão",
            tag = "NITIDEZ & FOCO",
            tagColor = successColors().content,
            doTip = "Mantenha o celular estável e paralelo à folha durante o clique.",
            dontTip = "Evite fotos inclinadas em ângulo diagonal (perspectiva distorcida)."
        ),
        CaptureTip(
            id = "framing",
            title = "Enquadramento Completo com Margens",
            description = "Capture os 4 cantos da página para o Gemini ler o CNPJ, nome da empresa ré e a data de vencimento.",
            icon = Icons.Outlined.CropFree,
            scoreBoost = "+20% Extração",
            tag = "ENQUADRAMENTO",
            tagColor = infoColors().content,
            doTip = "Apoie o documento sobre uma superfície plana com fundo contrastante (mesa escura).",
            dontTip = "Não corte o cabeçalho nem as notas explicativas de rodapé."
        ),
        CaptureTip(
            id = "digital_pdf",
            title = "Contratos e Boletos em PDF Digital",
            description = "Documentos digitais direto do app do banco geram precisão de 100% no cálculo de repetição em dobro.",
            icon = Icons.Outlined.PictureAsPdf,
            scoreBoost = "100% SCORE",
            tag = "DIGITAL",
            tagColor = filedColors().content,
            doTip = "Selecione o arquivo PDF original direto pela galeria / arquivos.",
            dontTip = "Evite tirar foto da tela do computador se você tiver o arquivo baixado."
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Dicas de Captura & Iluminação",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            val badgeSuccess = successColors()
            Surface(
                color = badgeSuccess.container,
                shape = Pill
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = badgeSuccess.content,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Score Alto (IA)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeSuccess.content,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(tips) { tip ->
                CaptureTipCard(
                    tip = tip,
                    onClick = { onTipClick(tip) }
                )
            }
        }
    }
}

@Composable
fun CaptureTipCard(
    tip: CaptureTip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(260.dp)
            .clickable { onClick() }
            .testTag("capture_tip_card_${tip.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Tag, Score and Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = tip.tagColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tip.tag,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = tip.tagColor
                        )
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = tip.scoreBoost,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Title & Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(tip.tagColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tip.icon,
                        contentDescription = null,
                        tint = tip.tagColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
            }

            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                ),
                maxLines = 2
            )

            // Do / Don't Pill
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = successColors().content,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = tip.doTip,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp
                        ),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

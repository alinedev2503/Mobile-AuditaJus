package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val title: String,
    val description: String,
    val imageUrl: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPageData(
            title = "Justiça na palma da mão",
            description = "Audite contratos e faturas com facilidade. Transforme documentos complexos em resumos claros e acionáveis para sua defesa.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBKzQP2MdkNNv58elLaZipJq82pIySn4jG3_p3iGg8oX23I6mrnw-KlyXW9y1kV7HUqO-XsnwFSzm5LadkcGJsdE7UQ6Vj_hUPa2YrZuNHRUjeLoapvu2kD4L5UKiMVzIXf-rUEx5PZ0r2mNpapGeoNVDJYCQJavuqkOvIFeZufP7W7XWqrnL6_G0_lKsLDsJ31Eapuer94rVVm_s2r2wrYRhVm1opADni0YSInos1Wn7iNpx_pK40D"
        ),
        OnboardingPageData(
            title = "Cálculos Precisos",
            description = "Deixe a matemática conosco. Realize cálculos automatizados de danos e juros para pequenas causas com precisão judicial.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBXmywlliwIvFGYHmaN8h6hXjq2L3DOf_cce1SnTkWEB0cMKtKFtBJ0Z9qEY9p3L5_Tvk3BUV4sZkTnJmxZmdqUwnRNJOcLOU-bCSxJPtFI_d_0ZupJj-HeEFG2MSYQ7ddYYUPlRubsTYaznwSNhkGy5KbahXa5LyjqrbnbOWNOwnCjX99NvCgYhUpeu57Zd84APVmVF3Y-B0FLQ_B6alzLszLqlwfg8xcKqjvAdtb3m3ItzrCDdsay"
        ),
        OnboardingPageData(
            title = "Petição Pronta",
            description = "Gere PDFs formatados e prontos para o tribunal com apenas um clique. Sua documentação estruturada e profissional instantaneamente.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCQqXga_EGdhUcVfH1BB70v9xJ2Ab0k_3PkiwQOLovFvT_VRH5baxBfn1htZLqd-ccgtkfVOBisG5ICcqBwEvhE3FBuy9iZQg8vPrtQXwFNoMSlYuURiqB1bnLQL_I1hA1RtVNQcaS3UGQK4V9e8yHDuZA_hG9TwTFciWeVh1ncuVOf6s7BHGF0i5qgGtymxo6vNDbjw7HLXp3VaqosyFLT54YBh-C7fnQxgZnAGPK9s3En6n4Bcgfa"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contador Jurídico Pro",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                AnimatedVisibility(
                    visible = pagerState.currentPage < pages.size - 1,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Pular",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pages.size - 1)
                                }
                            }
                            .padding(8.dp)
                            .testTag("skip_onboarding_button")
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == pagerState.currentPage
                        AnimatedContainer(isSelected = isSelected)
                    }
                }
                
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.setCompletedOnboarding(true)
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_next_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "Começar Agora" else "Próximo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (pagerState.currentPage == pages.size - 1) Icons.Default.CheckCircle else Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            val currentPage = pages[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                        .padding(bottom = 32.dp)
                ) {
                    AsyncImage(
                        model = currentPage.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp))
                    )
                }
                
                Text(
                    text = currentPage.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = currentPage.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AnimatedContainer(isSelected: Boolean) {
    val width = if (isSelected) 24.dp else 8.dp
    val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    
    Box(
        modifier = Modifier
            .size(width = width, height = 8.dp)
            .background(color = color, shape = CircleShape)
    )
}

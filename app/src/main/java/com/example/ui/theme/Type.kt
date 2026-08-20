package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// ============================================================================
// Both chosen design specs — "Contador Jurídico Pro" (light) and
// "Lex Dark Sovereign" (dark) — use Inter exclusively:
//   "This design system utilizes Inter exclusively to leverage its
//    exceptional legibility and systematic rhythm."
//
// Bundled as a single variable font (res/font/inter_variable.ttf, OFL-1.1,
// sourced from github.com/google/fonts) rather than fetched from the Google
// Fonts downloadable-fonts provider, since that path requires embedding the
// provider's signing-certificate hash and this app has no way to verify one
// here — a bundled, license-clean binary is the safer choice.
// ============================================================================

private fun interWeight(weight: FontWeight) = Font(
    resId = R.font.inter_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val Inter = FontFamily(
    interWeight(FontWeight.Normal),
    interWeight(FontWeight.Medium),
    interWeight(FontWeight.SemiBold),
    interWeight(FontWeight.Bold),
)

// Type scale merged from both specs' `typography:` front-matter (values are
// near-identical between the two; where they differ — e.g. headline-lg
// weight 700 vs 600 — the light-mode ("Contador Jurídico Pro") spec wins
// since it is the spec matching the app name).
val Typography = Typography(
    displayLarge = TextStyle( // display-lg
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.96).sp, // -0.02em @ 48sp
    ),
    headlineLarge = TextStyle( // headline-lg
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.32).sp, // -0.01em @ 32sp
    ),
    headlineMedium = TextStyle( // headline-md
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle( // headline-lg-mobile
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle( // body-lg
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle( // body-md
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodySmall = TextStyle( // body-sm (dark spec)
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle( // label-bold
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.14.sp, // 0.01em @ 14sp
    ),
    labelMedium = TextStyle( // label-md
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.6.sp, // 0.05em @ 12sp
    ),
    labelSmall = TextStyle( // label-sm
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

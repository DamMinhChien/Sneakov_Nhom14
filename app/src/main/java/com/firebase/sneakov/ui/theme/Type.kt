package com.firebase.sneakov.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.firebase.sneakov.R

// Set of Material typography styles to start with
val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold)
)
val DefaultTypography = Typography()

val Typography = DefaultTypography.copy(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = Nunito),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = Nunito),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = Nunito),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = Nunito),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = Nunito),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = Nunito),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = Nunito),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = Nunito),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = Nunito),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = Nunito),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = Nunito),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = Nunito),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = Nunito),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = Nunito),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = Nunito)
)
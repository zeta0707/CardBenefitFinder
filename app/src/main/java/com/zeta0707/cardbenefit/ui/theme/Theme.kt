package com.zeta0707.cardbenefit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green80 = Color(0xFF7FCB8F)
private val Green40 = Color(0xFF1B5E20)
private val GreenGrey40 = Color(0xFF4C6B52)

private val LightColors = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Green80
)

private val DarkColors = darkColorScheme(
    primary = Green80,
    secondary = GreenGrey40,
    tertiary = Green40
)

@Composable
fun CardBenefitFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

package com.pesamjanja.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PesaLightColors = lightColorScheme(
    primary = MpesaGreen,
    onPrimary = PureWhite,
    primaryContainer = MpesaGreenLight,
    onPrimaryContainer = MpesaGreenDark,
    secondary = PureBlack,
    onSecondary = PureWhite,
    background = OffWhite,
    onBackground = PureBlack,
    surface = PureWhite,
    onSurface = PureBlack,
    error = BrokeRed,
)

private val PesaDarkColors = darkColorScheme(
    primary = MpesaGreen,
    onPrimary = PureBlack,
    primaryContainer = MpesaGreenDark,
    onPrimaryContainer = MpesaGreenLight,
    secondary = PureWhite,
    onSecondary = PureBlack,
    background = Charcoal,
    onBackground = PureWhite,
    surface = PureBlack,
    onSurface = PureWhite,
    error = BrokeRed,
)

@Composable
fun PesaMjanjaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) PesaDarkColors else PesaLightColors
    MaterialTheme(
        colorScheme = colors,
        typography = PesaTypography,
        content = content
    )
}

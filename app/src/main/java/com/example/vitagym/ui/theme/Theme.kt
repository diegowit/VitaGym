package com.example.vitagym.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val VitaGymColorScheme = darkColorScheme(
    // Primary colors
    primary = PrimaryCyan,
    onPrimary = DarkNavy,
    primaryContainer = PrimaryCyanDark,
    onPrimaryContainer = TextPrimary,

    // Secondary colors
    secondary = SuccessGreen,
    onSecondary = DarkNavy,
    secondaryContainer = SuccessGreen,
    onSecondaryContainer = TextPrimary,

    // Tertiary colors
    tertiary = WarningAmber,
    onTertiary = DarkNavy,
    tertiaryContainer = WarningAmber,
    onTertiaryContainer = DarkNavy,

    // Error colors
    error = ErrorRed,
    onError = TextPrimary,
    errorContainer = ErrorRed,
    onErrorContainer = TextPrimary,

    // Background colors
    background = Background,
    onBackground = TextPrimary,

    // Surface colors
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = ElevatedDark,
    onSurfaceVariant = TextSecondary,

    // Outline
    outline = BorderLight,
    outlineVariant = BorderDark,
)


//Estudiar


@Composable
fun VitaGymTheme(
    darkTheme: Boolean = true, // VitaGym is always dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = VitaGymColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
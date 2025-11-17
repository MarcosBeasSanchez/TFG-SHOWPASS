package com.example.appmovilshowpass.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9FAFF5),        // azul claro para resaltar en dark mode
    onPrimary = Color(0xFF0A1B57),      // texto oscuro sobre primary
    primaryContainer = Color(0xFF1E3481), // azul base como contenedor
    onPrimaryContainer = Color(0xFFE0E6FF),

    secondary = Color(0xFFAAB8F0),
    onSecondary = Color(0xFF18224F),
    secondaryContainer = Color(0xFF6074B5),
    onSecondaryContainer = Color(0xFFDEE3FF),

    tertiary = Color(0xFFFFCC80),
    onTertiary = Color(0xFF3A2500),
    tertiaryContainer = Color(0xFFFFB74D),
    onTertiaryContainer = Color(0xFF000000),

    background = Color(0xFF101828),
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E2939),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF191F2F),

    error = Color(0xFFCF6679),
    onError = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E3481),        // azul base
    onPrimary = Color(0xFFFFFFFF),      // texto blanco sobre azul
    primaryContainer = Color(0xFF1E3481),
    onPrimaryContainer = Color(0xFF0A1B57),

    secondary = Color(0xFF6074B5),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF18224F),

    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFF3A2500),

    background = Color(0xFFDBEAFE),
    onBackground = Color(0xFF1A1A1A),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFD6E4FF),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun AppMovilShowpassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
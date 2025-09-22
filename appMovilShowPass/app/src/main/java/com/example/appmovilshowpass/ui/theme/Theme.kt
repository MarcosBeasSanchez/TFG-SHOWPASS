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
    primary = Color(0xFF000000),       // negro para TopAppBar, FAB, etc.
    secondary = Color(0xFF625B71),
    background = Color(0xFF1C1B1F),    // fondo oscuro en modo oscuro
    surface = Color(0xFF1C1B1F),
    error = Color(0xFFB3261E),
    onPrimary = Color.White,            // texto sobre primary
    onSecondary = Color.White,
    onBackground = Color.White,         // texto sobre fondo oscuro
    onSurface = Color.White,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFFFFFF),       // blanco para TopAppBar, FAB, etc.
    secondary = Color(0xC9CCC2DC),
    background = Color(0xFFFFFBFE),    // fondo claro en modo claro
    surface = Color(0xFFFFFBFE),
    error = Color(0xFFCF6679),
    onPrimary = Color.Black,            // texto sobre primary
    onSecondary = Color.Black,
    onBackground = Color.Black,         // texto sobre fondo claro
    onSurface = Color.Black,
    onError = Color.Black

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
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
        typography = Typography,
        content = content
    )
}
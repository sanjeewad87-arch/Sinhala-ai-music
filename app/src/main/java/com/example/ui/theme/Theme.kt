package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GlowPurple,
    secondary = GlowPink,
    tertiary = GlowPink,
    background = DarkBg,
    surface = GlassBase,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFF1F1F1)
)

private val LightColorScheme = DarkColorScheme // Always premium dark mode for beautiful music studio vibe

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark mode
    dynamicColor: Boolean = false, // Use our branded palette
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

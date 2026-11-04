package com.example.gestionclinica.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondaryContainer = SlateOutlineVariant,
    onSecondaryContainer = SlateText,
    background = SlateBackground,
    onBackground = SlateText,
    surface = Color.White,
    onSurface = SlateText,
    surfaceVariant = SlateContainer,
    onSurfaceVariant = SlateTextSecondary,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = SlateContainer,
    outline = SlateOutline,
    outlineVariant = SlateOutlineVariant,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = TealOnPrimaryContainer,
    primaryContainer = TealPrimaryContainerDark,
    onPrimaryContainer = TealOnPrimaryContainerDark,
    secondaryContainer = NavyOutlineVariant,
    onSecondaryContainer = NavyText,
    background = NavyBackground,
    onBackground = NavyText,
    surface = NavySurface,
    onSurface = NavyText,
    surfaceVariant = NavyContainer,
    onSurfaceVariant = NavyTextSecondary,
    surfaceContainerLowest = NavySurface,
    surfaceContainerLow = NavySurface,
    surfaceContainer = NavyContainer,
    outline = NavyOutline,
    outlineVariant = NavyOutlineVariant,
    error = ErrorRedDark
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun GestionClinicaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
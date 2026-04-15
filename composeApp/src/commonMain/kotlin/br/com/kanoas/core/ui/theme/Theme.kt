package br.com.kanoas.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// =============================================================================
// Kanoas Theme
// =============================================================================

private val LightColorScheme = lightColorScheme(
    primary = KanoasPrimary,
    onPrimary = KanoasOnPrimary,
    primaryContainer = KanoasPrimaryContainer,
    onPrimaryContainer = KanoasOnPrimaryContainer,
    secondary = KanoasSecondary,
    onSecondary = KanoasOnSecondary,
    secondaryContainer = KanoasSecondaryContainer,
    onSecondaryContainer = KanoasOnSecondaryContainer,
    tertiary = KanoasTertiary,
    onTertiary = KanoasOnTertiary,
    tertiaryContainer = KanoasTertiaryContainer,
    onTertiaryContainer = KanoasOnTertiaryContainer,
    error = KanoasError,
    onError = KanoasOnError,
    errorContainer = KanoasErrorContainer,
    onErrorContainer = KanoasOnErrorContainer,
    background = KanoasBackground,
    onBackground = KanoasOnBackground,
    surface = KanoasSurface,
    onSurface = KanoasOnSurface,
    surfaceVariant = KanoasSurfaceVariant,
    onSurfaceVariant = KanoasOnSurfaceVariant,
    outline = KanoasOutline,
)

private val DarkColorScheme = darkColorScheme(
    primary = KanoasDarkPrimary,
    onPrimary = KanoasDarkOnPrimary,
    primaryContainer = KanoasDarkPrimaryContainer,
    onPrimaryContainer = KanoasDarkOnPrimaryContainer,
    secondary = KanoasDarkSecondary,
    onSecondary = KanoasDarkOnSecondary,
    secondaryContainer = KanoasDarkSecondaryContainer,
    onSecondaryContainer = KanoasDarkOnSecondaryContainer,
    tertiary = KanoasDarkTertiary,
    onTertiary = KanoasDarkOnTertiary,
    tertiaryContainer = KanoasDarkTertiaryContainer,
    onTertiaryContainer = KanoasDarkOnTertiaryContainer,
    error = KanoasDarkError,
    onError = KanoasDarkOnError,
    errorContainer = KanoasDarkErrorContainer,
    onErrorContainer = KanoasDarkOnErrorContainer,
    background = KanoasDarkBackground,
    onBackground = KanoasDarkOnBackground,
    surface = KanoasDarkSurface,
    onSurface = KanoasDarkOnSurface,
    surfaceVariant = KanoasDarkSurfaceVariant,
    onSurfaceVariant = KanoasDarkOnSurfaceVariant,
    outline = KanoasDarkOutline,
)

/**
 * Tema Kanoas com suporte a dark/light mode.
 */
@Composable
fun KanoasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KanoasTypography,
        content = content,
    )
}

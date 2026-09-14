package com.soczuks.footballassistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PitchPrimaryDark,
    onPrimary = PitchOnPrimaryDark,
    primaryContainer = PitchPrimaryContainerDark,
    onPrimaryContainer = PitchOnPrimaryContainerDark,
    secondary = AccentSecondaryDark,
    onSecondary = AccentOnSecondaryDark,
    secondaryContainer = AccentSecondaryContainerDark,
    onSecondaryContainer = AccentOnSecondaryContainerDark,
    tertiary = ClubTertiaryDark,
    onTertiary = ClubOnTertiaryDark,
    tertiaryContainer = ClubTertiaryContainerDark,
    onTertiaryContainer = ClubOnTertiaryContainerDark,
    background = StadiumBackgroundDark,
    onBackground = StadiumOnBackgroundDark,
    surface = StadiumSurfaceDark,
    onSurface = StadiumOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PitchPrimary,
    onPrimary = PitchOnPrimary,
    primaryContainer = PitchPrimaryContainer,
    onPrimaryContainer = PitchOnPrimaryContainer,
    secondary = AccentSecondary,
    onSecondary = AccentOnSecondary,
    secondaryContainer = AccentSecondaryContainer,
    onSecondaryContainer = AccentOnSecondaryContainer,
    tertiary = ClubTertiary,
    onTertiary = ClubOnTertiary,
    tertiaryContainer = ClubTertiaryContainer,
    onTertiaryContainer = ClubOnTertiaryContainer,
    background = StadiumBackground,
    onBackground = StadiumOnBackground,
    surface = StadiumSurface,
    onSurface = StadiumOnSurface
)

@Composable
fun FootballAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false to prioritize our custom football theme over dynamic system colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
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

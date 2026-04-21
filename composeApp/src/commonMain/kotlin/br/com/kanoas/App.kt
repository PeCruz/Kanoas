package br.com.kanoas

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.kanoas.core.ui.theme.KanoasTheme
import br.com.kanoas.navigation.NavGraph
import br.com.kanoas.presentation.core.theme.ThemeMode
import br.com.kanoas.presentation.core.theme.ThemeViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        val themeViewModel: ThemeViewModel = koinViewModel()
        val themeState by themeViewModel.state.collectAsState()
        val systemDark = isSystemInDarkTheme()

        val isDark = when (themeState.mode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> systemDark
        }

        KanoasTheme(darkTheme = isDark) {
            NavGraph(themeViewModel = themeViewModel)
        }
    }
}
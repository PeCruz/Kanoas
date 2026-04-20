package br.com.kanoas.core.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.com.kanoas.presentation.core.theme.ThemeIntent
import br.com.kanoas.presentation.core.theme.ThemeMode
import br.com.kanoas.presentation.core.theme.ThemeViewModel

/**
 * Botão reutilizável para alternar entre tema claro e escuro.
 * Exibe ícone de sol (☀) no modo escuro e lua (🌙) no modo claro.
 *
 * Deve ser usado em todas as telas/activities do app.
 */
@Composable
fun ThemeToggleButton(
    themeViewModel: ThemeViewModel,
) {
    val themeState by themeViewModel.state.collectAsState()

    val isDark = themeState.mode == ThemeMode.DARK

    IconButton(
        onClick = { themeViewModel.handleIntent(ThemeIntent.Toggle) },
    ) {
        // Usa text-based icons (sem dependência de extended icons)
        Text(
            text = if (isDark) "☀" else "🌙",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

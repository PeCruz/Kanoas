package br.com.sprena.presentation.core.theme

import br.com.sprena.shared.core.mvi.UiState

/**
 * Modo de tema suportado pelo app.
 *  - [SYSTEM] — segue o tema do SO
 *  - [LIGHT]  — força claro
 *  - [DARK]   — força escuro
 */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class ThemeState(
    val mode: ThemeMode = ThemeMode.SYSTEM,
) : UiState

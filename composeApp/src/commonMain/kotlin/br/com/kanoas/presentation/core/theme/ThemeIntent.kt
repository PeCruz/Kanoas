package br.com.kanoas.presentation.core.theme

import br.com.kanoas.shared.core.mvi.UiIntent

sealed interface ThemeIntent : UiIntent {
    /** Alterna Light ↔ Dark (SYSTEM é tratado como Light para o primeiro toggle). */
    data object Toggle : ThemeIntent
    data class Set(val mode: ThemeMode) : ThemeIntent
}

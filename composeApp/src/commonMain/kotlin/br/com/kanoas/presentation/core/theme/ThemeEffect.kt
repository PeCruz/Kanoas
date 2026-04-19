package br.com.kanoas.presentation.core.theme

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface ThemeEffect : UiEffect {
    data class ThemeChanged(val mode: ThemeMode) : ThemeEffect
}

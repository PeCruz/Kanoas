package br.com.kanoas.presentation.bar

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface BarEffect : UiEffect {
    data object OpenAddClientDialog : BarEffect
    data class ShowError(val message: String) : BarEffect
}

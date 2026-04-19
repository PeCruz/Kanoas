package br.com.kanoas.presentation.login

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface LoginEffect : UiEffect {
    data object NavigateHome : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}

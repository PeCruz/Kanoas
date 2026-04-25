package br.com.sprena.presentation.login

import br.com.sprena.shared.core.mvi.UiEffect

sealed interface LoginEffect : UiEffect {
    data object NavigateHome : LoginEffect
    data class ShowError(val message: String) : LoginEffect
}

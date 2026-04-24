package br.com.kanoas.presentation.bar.addclient

import br.com.kanoas.presentation.bar.BarClient
import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface AddClientEffect : UiEffect {
    data class ClientCreated(val client: BarClient) : AddClientEffect
    data class ShowError(val message: String) : AddClientEffect
    data object Dismissed : AddClientEffect
}

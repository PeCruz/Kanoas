package br.com.kanoas.presentation.bar.clientdetail

import br.com.kanoas.presentation.bar.BarClient
import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface ClientDetailEffect : UiEffect {
    data class ClientUpdated(val client: BarClient) : ClientDetailEffect
    data class ClientDeleted(val clientId: String) : ClientDetailEffect
    data class ShowError(val message: String) : ClientDetailEffect
    data object Dismissed : ClientDetailEffect
}

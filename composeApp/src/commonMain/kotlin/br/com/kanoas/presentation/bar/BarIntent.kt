package br.com.kanoas.presentation.bar

import br.com.kanoas.shared.core.mvi.UiIntent

sealed interface BarIntent : UiIntent {
    data object Load : BarIntent
    data class SearchQueryChanged(val query: String) : BarIntent
    data object AddClientClicked : BarIntent
    data object DismissAddClientDialog : BarIntent
    data class ClientClicked(val client: BarClient) : BarIntent
    data object DismissClientDetail : BarIntent
    data class ClientAdded(val client: BarClient) : BarIntent
    data class ClientUpdated(val client: BarClient) : BarIntent
    data class TogglePaid(val clientId: String) : BarIntent
    data class ClientDeleted(val clientId: String) : BarIntent
}

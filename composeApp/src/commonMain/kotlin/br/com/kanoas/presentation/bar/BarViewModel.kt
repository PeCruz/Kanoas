package br.com.kanoas.presentation.bar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel da tela principal do Bar — gerencia lista de clientes,
 * busca, e estado de diálogos.
 */
class BarViewModel :
    ViewModel(),
    MviViewModel<BarState, BarIntent, BarEffect> {

    private val _state = MutableStateFlow(BarState())
    override val state: StateFlow<BarState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<BarEffect>()
    override val effects: SharedFlow<BarEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: BarIntent) {
        when (intent) {
            is BarIntent.Load -> { /* future: load from repository */ }

            is BarIntent.SearchQueryChanged -> {
                val query = intent.query
                val filtered = applyFilter(_state.value.clients, query)
                _state.value = _state.value.copy(
                    searchQuery = query,
                    filteredClients = filtered,
                )
            }

            is BarIntent.AddClientClicked -> {
                _state.value = _state.value.copy(isAddClientDialogVisible = true)
            }

            is BarIntent.DismissAddClientDialog -> {
                _state.value = _state.value.copy(isAddClientDialogVisible = false)
            }

            is BarIntent.ClientClicked -> {
                _state.value = _state.value.copy(selectedClient = intent.client)
            }

            is BarIntent.DismissClientDetail -> {
                _state.value = _state.value.copy(selectedClient = null)
            }

            is BarIntent.ClientAdded -> {
                val updated = _state.value.clients + intent.client
                _state.value = _state.value.copy(
                    clients = updated,
                    filteredClients = applyFilter(updated, _state.value.searchQuery),
                )
            }

            is BarIntent.ClientUpdated -> {
                val updated = _state.value.clients.map { c ->
                    if (c.id == intent.client.id) intent.client else c
                }
                _state.value = _state.value.copy(
                    clients = updated,
                    filteredClients = applyFilter(updated, _state.value.searchQuery),
                    // NÃO atualizar selectedClient aqui — o ClientDetailViewModel
                    // gerencia seu próprio state internamente. Atualizar selectedClient
                    // causaria recomposição que recria o VM e perde o estado.
                )
            }

            is BarIntent.TogglePaid -> {
                val updated = _state.value.clients.map { c ->
                    if (c.id == intent.clientId) c.copy(isPaid = !c.isPaid) else c
                }
                _state.value = _state.value.copy(
                    clients = updated,
                    filteredClients = applyFilter(updated, _state.value.searchQuery),
                )
            }

            is BarIntent.ClientDeleted -> {
                val updated = _state.value.clients.filter { it.id != intent.clientId }
                _state.value = _state.value.copy(
                    clients = updated,
                    filteredClients = applyFilter(updated, _state.value.searchQuery),
                    selectedClient = if (_state.value.selectedClient?.id == intent.clientId) {
                        null
                    } else {
                        _state.value.selectedClient
                    },
                )
            }
        }
    }

    private fun applyFilter(clients: List<BarClient>, query: String): List<BarClient> {
        if (query.isBlank()) return clients
        val lowerQuery = query.lowercase()
        return clients.filter { client ->
            client.name.lowercase().contains(lowerQuery) ||
                (client.nickname?.lowercase()?.contains(lowerQuery) == true)
        }
    }
}

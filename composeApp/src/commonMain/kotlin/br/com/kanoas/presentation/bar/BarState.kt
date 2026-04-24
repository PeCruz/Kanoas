package br.com.kanoas.presentation.bar

import br.com.kanoas.shared.core.mvi.UiState

/**
 * Representa um cliente na tabela do Bar.
 */
data class BarClient(
    val id: String,
    val name: String,
    val nickname: String? = null,
    val phone: String,
    val cpf: String,
    val email: String? = null,
    val items: List<BarItem> = emptyList(),
    val isPaid: Boolean = false,
)

/**
 * Item consumido por um cliente.
 */
data class BarItem(
    val id: String,
    val name: String,
    val priceCents: Long,
    val quantity: Int = 1,
)

/**
 * State da tela principal do Bar — lista de clientes com busca.
 */
data class BarState(
    val clients: List<BarClient> = emptyList(),
    val searchQuery: String = "",
    val filteredClients: List<BarClient> = emptyList(),
    val isLoading: Boolean = false,
    val isAddClientDialogVisible: Boolean = false,
    val selectedClient: BarClient? = null,
    val error: String? = null,
) : UiState

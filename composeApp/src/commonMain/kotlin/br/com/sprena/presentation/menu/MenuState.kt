package br.com.sprena.presentation.menu

import br.com.sprena.shared.core.mvi.UiState

/**
 * Item do cardápio.
 */
data class MenuItem(
    val id: String,
    val name: String,
    val priceCents: Long,
    val description: String? = null,
)

/**
 * State da tela de Cardápio — lista de itens com busca e dialogs.
 */
data class MenuState(
    val items: List<MenuItem> = emptyList(),
    val searchQuery: String = "",
    val filteredItems: List<MenuItem> = emptyList(),
    val isLoading: Boolean = false,
    val isAddDialogVisible: Boolean = false,
    val selectedItem: MenuItem? = null,
    val error: String? = null,
) : UiState

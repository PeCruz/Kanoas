package br.com.sprena.presentation.category

import br.com.sprena.shared.core.mvi.UiState

/**
 * State da tela de Categorias Financeiras.
 */
data class CategoryState(
    val categories: List<String> = emptyList(),
    val isAddDialogVisible: Boolean = false,
    val editingCategory: String? = null,
    val error: String? = null,
) : UiState

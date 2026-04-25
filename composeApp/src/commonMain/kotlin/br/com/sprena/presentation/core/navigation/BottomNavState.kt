package br.com.sprena.presentation.core.navigation

import br.com.sprena.shared.core.mvi.UiState

/**
 * Abas da barra inferior de navegação.
 * Ordem: Home (clientes esportes), Quadro (kanban), Comandas (bar),
 * Financeiro, Config.
 */
enum class BottomTab { HOME, QUADRO, BAR, FINANCIAL, SETTINGS }

data class BottomNavState(
    val current: BottomTab = BottomTab.HOME,
) : UiState

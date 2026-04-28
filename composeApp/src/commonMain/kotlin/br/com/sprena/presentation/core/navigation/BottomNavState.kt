package br.com.sprena.presentation.core.navigation

import br.com.sprena.shared.core.mvi.UiState

/**
 * Abas da barra inferior de navegação.
 * Ordem: Home (clientes esportes), Eventos, Comandas (bar),
 * Financeiro, Config.
 */
enum class BottomTab { HOME, EVENTOS, BAR, FINANCIAL, SETTINGS }

data class BottomNavState(
    val current: BottomTab = BottomTab.HOME,
) : UiState

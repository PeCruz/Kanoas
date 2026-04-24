package br.com.kanoas.presentation.core.navigation

import br.com.kanoas.shared.core.mvi.UiState

/**
 * Abas da barra inferior de navegação.
 * Sugestão inicial: Kanban (quadro) e Financial (finanças).
 */
enum class BottomTab { KANBAN, FINANCIAL, BAR }

data class BottomNavState(
    val current: BottomTab = BottomTab.KANBAN,
) : UiState

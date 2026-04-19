package br.com.kanoas.presentation.core.navigation

import br.com.kanoas.shared.core.mvi.UiIntent

sealed interface BottomNavIntent : UiIntent {
    data class TabSelected(val tab: BottomTab) : BottomNavIntent
}

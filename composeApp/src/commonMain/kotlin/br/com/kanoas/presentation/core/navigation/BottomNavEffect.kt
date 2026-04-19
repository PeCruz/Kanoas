package br.com.kanoas.presentation.core.navigation

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface BottomNavEffect : UiEffect {
    data class NavigateTo(val tab: BottomTab) : BottomNavEffect
}

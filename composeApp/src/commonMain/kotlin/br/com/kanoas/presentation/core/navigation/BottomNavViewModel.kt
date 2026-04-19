package br.com.kanoas.presentation.core.navigation

import androidx.lifecycle.ViewModel
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class BottomNavViewModel :
    ViewModel(),
    MviViewModel<BottomNavState, BottomNavIntent, BottomNavEffect> {

    private val _state = MutableStateFlow(BottomNavState())
    override val state: StateFlow<BottomNavState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<BottomNavEffect>()
    override val effects: SharedFlow<BottomNavEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: BottomNavIntent) {
        TODO("Day 3 TDD — implementar após Red")
    }
}

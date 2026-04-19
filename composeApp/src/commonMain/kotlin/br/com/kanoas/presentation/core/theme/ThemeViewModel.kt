package br.com.kanoas.presentation.core.theme

import androidx.lifecycle.ViewModel
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel :
    ViewModel(),
    MviViewModel<ThemeState, ThemeIntent, ThemeEffect> {

    private val _state = MutableStateFlow(ThemeState())
    override val state: StateFlow<ThemeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ThemeEffect>()
    override val effects: SharedFlow<ThemeEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: ThemeIntent) {
        TODO("Day 3 TDD — implementar após Red")
    }
}

package br.com.kanoas.presentation.login

import androidx.lifecycle.ViewModel
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel(), MviViewModel<LoginState, LoginIntent, LoginEffect> {

    private val _state = MutableStateFlow(LoginState())
    override val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffect>()
    override val effects: SharedFlow<LoginEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: LoginIntent) {
        TODO("Day 3 TDD — implementar após Red")
    }
}

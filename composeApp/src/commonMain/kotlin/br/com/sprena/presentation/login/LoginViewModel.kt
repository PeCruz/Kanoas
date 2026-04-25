package br.com.sprena.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.sprena.shared.auth.domain.validation.LoginValidator
import br.com.sprena.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(), MviViewModel<LoginState, LoginIntent, LoginEffect> {

    private val _state = MutableStateFlow(LoginState())
    override val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffect>()
    override val effects: SharedFlow<LoginEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UsernameChanged -> {
                val validation = LoginValidator.validateUsername(intent.value)
                _state.value = _state.value.copy(
                    username = intent.value,
                    usernameError = validation.errorMessage,
                    canSubmit = canSubmit(intent.value, _state.value.password),
                )
            }

            is LoginIntent.PasswordChanged -> {
                val validation = LoginValidator.validatePassword(intent.value)
                _state.value = _state.value.copy(
                    password = intent.value,
                    passwordError = validation.errorMessage,
                    canSubmit = canSubmit(_state.value.username, intent.value),
                )
            }

            is LoginIntent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible,
                )
            }

            is LoginIntent.Submit -> {
                val uResult = LoginValidator.validateUsername(_state.value.username)
                val pResult = LoginValidator.validatePassword(_state.value.password)

                if (uResult.isValid && pResult.isValid) {
                    viewModelScope.launch {
                        _effects.emit(LoginEffect.NavigateHome)
                    }
                } else {
                    _state.value = _state.value.copy(
                        usernameError = uResult.errorMessage,
                        passwordError = pResult.errorMessage,
                    )
                    viewModelScope.launch {
                        _effects.emit(LoginEffect.ShowError("Verifique os campos"))
                    }
                }
            }
        }
    }

    private fun canSubmit(username: String, password: String): Boolean =
        LoginValidator.validateUsername(username).isValid &&
            LoginValidator.validatePassword(password).isValid
}
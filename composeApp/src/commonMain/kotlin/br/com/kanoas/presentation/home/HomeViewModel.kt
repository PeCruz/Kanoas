package br.com.kanoas.presentation.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado da tela Home — imutável, seguindo UDF.
 */
data class HomeUiState(
    val title: String = "Kanoas",
    val subtitle: String = "AI Jail • Isolamento & Governança",
    val isLoading: Boolean = false,
)

/**
 * Eventos que a UI pode disparar.
 */
sealed interface HomeUiEvent {
    data object OnRefresh : HomeUiEvent
}

/**
 * ViewModel da Home — gerencia estado via StateFlow.
 *
 * Segue o padrão UDF (Unidirectional Data Flow):
 * UI observa [uiState] e emite eventos via [onEvent].
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnRefresh -> handleRefresh()
        }
    }

    private fun handleRefresh() {
        // Future: trigger data refresh via Use Case
        _uiState.value = _uiState.value.copy(isLoading = false)
    }
}

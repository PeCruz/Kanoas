package br.com.kanoas.shared.core.mvi

/**
 * Marcador para classes de estado MVI.
 *
 * Toda implementação deve ser uma `data class` imutável que representa
 * o snapshot completo da UI em um determinado momento.
 *
 * Exemplo:
 * ```kotlin
 * data class KanbanState(
 *     val boards: List<Board> = emptyList(),
 *     val isLoading: Boolean = false,
 *     val error: String? = null,
 * ) : UiState
 * ```
 */
interface UiState

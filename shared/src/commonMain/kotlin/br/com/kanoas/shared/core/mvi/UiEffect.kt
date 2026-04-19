package br.com.kanoas.shared.core.mvi

/**
 * Marcador para classes de efeito colateral MVI (one-shot events).
 *
 * Efeitos são emitidos via `SharedFlow` e consumidos uma única vez pela UI.
 * Use para: navegação, toasts, snackbars, dialogs, vibração, etc.
 *
 * Exemplo:
 * ```kotlin
 * sealed interface KanbanEffect : UiEffect {
 *     data class NavigateToBoard(val boardId: String) : KanbanEffect
 *     data class ShowError(val message: String) : KanbanEffect
 *     data object TaskMovedSuccess : KanbanEffect
 * }
 * ```
 */
interface UiEffect

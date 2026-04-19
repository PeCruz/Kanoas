package br.com.kanoas.shared.core.mvi

/**
 * Marcador para classes de intenção MVI.
 *
 * Toda implementação deve ser um `sealed interface` que descreve
 * TUDO que o usuário pode fazer naquela tela.
 *
 * Exemplo:
 * ```kotlin
 * sealed interface KanbanIntent : UiIntent {
 *     data object LoadBoards : KanbanIntent
 *     data class MoveTask(val taskId: String, val targetColumnId: String) : KanbanIntent
 *     data class CreateBoard(val name: String) : KanbanIntent
 * }
 * ```
 */
interface UiIntent

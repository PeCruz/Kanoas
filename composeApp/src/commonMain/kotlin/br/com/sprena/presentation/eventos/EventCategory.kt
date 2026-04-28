package br.com.sprena.presentation.eventos

/**
 * Categorias de evento — cada uma corresponde a uma tab na tela Eventos.
 *
 * REALIZADOS é a tab especial que recebe eventos com data expirada automaticamente.
 *
 * @property label texto exibido na UI (tab e badge).
 */
enum class EventCategory(val label: String) {
    EVENTOS("Eventos"),
    ALUGUEL("Aluguel"),
    DAY_USE("Day Use"),
    REALIZADOS("Realizados"),
}

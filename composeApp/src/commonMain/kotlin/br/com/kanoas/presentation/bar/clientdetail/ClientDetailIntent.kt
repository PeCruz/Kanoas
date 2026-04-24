package br.com.kanoas.presentation.bar.clientdetail

import br.com.kanoas.shared.core.mvi.UiIntent

sealed interface ClientDetailIntent : UiIntent {
    data object AddItemClicked : ClientDetailIntent
    data object DismissAddItem : ClientDetailIntent
    data class MenuItemSelected(val menuItem: MenuItem) : ClientDetailIntent
    data class NewItemNameChanged(val name: String) : ClientDetailIntent
    data class NewItemPriceChanged(val priceCents: Long?) : ClientDetailIntent
    data object SaveItem : ClientDetailIntent
    data class RemoveItem(val itemId: String) : ClientDetailIntent
    data class IncrementItem(val itemId: String) : ClientDetailIntent
    data class DecrementItem(val itemId: String) : ClientDetailIntent
    data object ConfirmDeleteItem : ClientDetailIntent
    data object CancelDeleteItem : ClientDetailIntent
    data object NextItemsPage : ClientDetailIntent
    data object PrevItemsPage : ClientDetailIntent
    data object TogglePaid : ClientDetailIntent
    data object DeleteClicked : ClientDetailIntent
    data object DeleteConfirmed : ClientDetailIntent
    data object DeleteCancelled : ClientDetailIntent
    data object Dismiss : ClientDetailIntent
}

/**
 * Item pré-definido do cardápio.
 */
data class MenuItem(
    val name: String,
    val priceCents: Long,
)

/**
 * Cardápio padrão do Bar.
 */
val DEFAULT_MENU_ITEMS = listOf(
    MenuItem(name = "Agua", priceCents = 500),
    MenuItem(name = "Almoço", priceCents = 2500),
    MenuItem(name = "Gatorade", priceCents = 1000),
    MenuItem(name = "Xeque-Mate", priceCents = 1250),
)

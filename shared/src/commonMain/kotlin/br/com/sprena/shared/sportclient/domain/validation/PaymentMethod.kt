package br.com.sprena.shared.sportclient.domain.validation

/**
 * Formas de pagamento aceitas para clientes de esportes.
 *
 * WELLHUB e TOTALPASS exigem um valor adicional em dinheiro (cashAmountCents).
 * CASH não exige valor adicional.
 */
enum class PaymentMethod {
    WELLHUB,
    TOTALPASS,
    CASH,
}

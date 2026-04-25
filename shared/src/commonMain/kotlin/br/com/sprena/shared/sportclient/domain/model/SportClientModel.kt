package br.com.sprena.shared.sportclient.domain.model

import br.com.sprena.shared.sportclient.domain.validation.PaymentMethod
import br.com.sprena.shared.sportclient.domain.validation.SportModality

/**
 * Entidade de domínio pura — representa um cliente de esportes.
 *
 * Campos estáticos (enums in-app): [modality], [paymentMethod]
 * Campos dinâmicos (Firestore): todos os demais
 */
data class SportClientModel(
    val id: String = "",
    val name: String,
    val apelido: String = "",
    val cpf: String,
    val phone: String,
    val modality: SportModality,
    val attendance: Int,
    val paymentMethod: PaymentMethod,
    val cashAmountCents: Long,
    val lastPaymentMonth: String,
)

package br.com.kanoas.shared.financial.domain.validation

import br.com.kanoas.shared.core.validation.ValidationResult

/**
 * Regras de validação para criação de Transação Financeira.
 *
 *  - PersonName: obrigatório, máx. 50 chars
 *  - Description: opcional, máx. 3000 chars
 */
object TransactionValidator {

    const val PERSON_NAME_MAX_LENGTH: Int = 50
    const val DESCRIPTION_MAX_LENGTH: Int = 3000

    fun validatePersonName(name: String): ValidationResult {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult.invalid("Nome é obrigatório")
            name.length > PERSON_NAME_MAX_LENGTH ->
                ValidationResult.invalid("Máximo de $PERSON_NAME_MAX_LENGTH caracteres")
            else -> ValidationResult.Valid
        }
    }

    fun validateDescription(description: String?): ValidationResult = when {
        description == null || description.isEmpty() -> ValidationResult.Valid
        description.length > DESCRIPTION_MAX_LENGTH ->
            ValidationResult.invalid("Máximo de $DESCRIPTION_MAX_LENGTH caracteres")
        else -> ValidationResult.Valid
    }
}

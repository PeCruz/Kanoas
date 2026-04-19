package br.com.kanoas.shared.financial.domain.validation

import br.com.kanoas.shared.core.validation.ValidationResult

/**
 * Regras de validação para criação de Transação Financeira.
 *
 * Espec (Day 3 TDD):
 *  - Amount: obrigatório, > 0 (validado via BrlMonetaryFormatter)
 *  - PersonName: obrigatório, máx. 50 chars
 *  - Description: opcional, máx. 3000 chars
 */
object TransactionValidator {

    const val PERSON_NAME_MAX_LENGTH: Int = 50
    const val DESCRIPTION_MAX_LENGTH: Int = 3000

    fun validatePersonName(name: String): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    fun validateDescription(description: String?): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }
}

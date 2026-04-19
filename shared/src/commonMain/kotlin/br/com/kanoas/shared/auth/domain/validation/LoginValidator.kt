package br.com.kanoas.shared.auth.domain.validation

import br.com.kanoas.shared.core.validation.ValidationResult

/**
 * Validações do formulário de Login.
 *
 * Espec (Day 3 TDD):
 *  - Username: obrigatório, mínimo 3 chars, máximo 50
 *  - Password: obrigatório, mínimo 6 chars
 */
object LoginValidator {

    const val USERNAME_MIN_LENGTH: Int = 3
    const val USERNAME_MAX_LENGTH: Int = 50
    const val PASSWORD_MIN_LENGTH: Int = 6

    fun validateUsername(username: String): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    fun validatePassword(password: String): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }
}

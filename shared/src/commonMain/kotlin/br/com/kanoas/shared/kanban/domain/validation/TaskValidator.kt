package br.com.kanoas.shared.kanban.domain.validation

import br.com.kanoas.shared.core.validation.ValidationResult

/**
 * Regras de validação para criação/edição de Task.
 *
 * Espec (Day 3 TDD):
 *  - Name: obrigatório, máx. 50 chars
 *  - Priority: obrigatório, 1..5
 *  - Description: opcional, máx. 3000 chars
 *  - Comment: opcional, máx. 2000 chars
 *  - Attachment: máx. 100 MB (100 * 1024 * 1024 bytes)
 *  - Start Date: dia atual
 *  - End Date: obrigatório, >= hoje
 *
 * Datas são representadas em *epoch days* (Long) — independe de plataforma.
 */
object TaskValidator {

    const val NAME_MAX_LENGTH: Int = 50
    const val DESCRIPTION_MAX_LENGTH: Int = 3000
    const val COMMENT_MAX_LENGTH: Int = 2000
    const val ATTACHMENT_MAX_BYTES: Long = 100L * 1024L * 1024L // 100 MB
    const val PRIORITY_MIN: Int = 1
    const val PRIORITY_MAX: Int = 5

    fun validateName(name: String): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    fun validatePriority(priority: Int?): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    fun validateDescription(description: String?): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    fun validateComment(comment: String?): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    fun validateAttachmentSize(sizeBytes: Long): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    /**
     * @param endEpochDay data-fim em epoch days
     * @param todayEpochDay dia atual em epoch days
     */
    fun validateEndDate(endEpochDay: Long?, todayEpochDay: Long): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }

    fun validateStartDate(startEpochDay: Long, todayEpochDay: Long): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }
}

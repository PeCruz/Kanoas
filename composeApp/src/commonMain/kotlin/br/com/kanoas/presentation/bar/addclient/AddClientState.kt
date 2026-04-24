package br.com.kanoas.presentation.bar.addclient

import br.com.kanoas.shared.core.mvi.UiState

/**
 * State do diálogo de adicionar cliente ao Bar.
 */
data class AddClientState(
    val name: String = "",
    val nickname: String = "",
    val phone: String = "",
    val cpf: String = "",
    val email: String = "",
    val nameError: String? = null,
    val phoneError: String? = null,
    val cpfError: String? = null,
    val emailError: String? = null,
    val isSaving: Boolean = false,
) : UiState

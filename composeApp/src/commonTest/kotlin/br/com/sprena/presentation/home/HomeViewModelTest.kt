package br.com.sprena.presentation.home

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Testes unitários do HomeViewModel.
 * Seguindo TDD — estes testes definem o comportamento esperado.
 */
class HomeViewModelTest {

    @Test
    fun `initial state has correct title`() = runTest {
        val viewModel = HomeViewModel()

        val state = viewModel.uiState.first()

        assertEquals("Sprena", state.title)
    }

    @Test
    fun `initial state has correct subtitle`() = runTest {
        val viewModel = HomeViewModel()

        val state = viewModel.uiState.first()

        assertEquals("AI Jail • Isolamento & Governança", state.subtitle)
    }

    @Test
    fun `initial state is not loading`() = runTest {
        val viewModel = HomeViewModel()

        val state = viewModel.uiState.first()

        assertFalse(state.isLoading)
    }

    @Test
    fun `onRefresh event updates state`() = runTest {
        val viewModel = HomeViewModel()

        viewModel.onEvent(HomeUiEvent.OnRefresh)

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
    }
}

package br.com.kanoas.di

import br.com.kanoas.presentation.core.navigation.BottomNavViewModel
import br.com.kanoas.presentation.core.theme.ThemeViewModel
import br.com.kanoas.presentation.financial.FinancialViewModel
import br.com.kanoas.presentation.financial.addtransaction.AddTransactionViewModel
import br.com.kanoas.presentation.home.HomeViewModel
import br.com.kanoas.presentation.kanban.KanbanViewModel
import br.com.kanoas.presentation.kanban.addtask.AddTaskViewModel
import br.com.kanoas.presentation.login.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Módulo Koin principal da aplicação.
 * Registra todos os ViewModels e dependências de presentation.
 */
fun appModule() = module {
    // --- ViewModels ---
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::KanbanViewModel)
    viewModelOf(::AddTaskViewModel)
    viewModelOf(::FinancialViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::BottomNavViewModel)
    viewModelOf(::ThemeViewModel)

    // --- Use Cases ---
    // Future: singleOf(::GetUserProfileUseCase)

    // --- Repositories ---
    // Future: singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}

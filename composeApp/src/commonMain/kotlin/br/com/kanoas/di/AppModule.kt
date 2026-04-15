package br.com.kanoas.di

import br.com.kanoas.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Módulo Koin principal da aplicação.
 * Registra ViewModels e dependências compartilhadas.
 */
fun appModule() = module {
    // --- ViewModels ---
    viewModelOf(::HomeViewModel)

    // --- Use Cases ---
    // Future: singleOf(::GetUserProfileUseCase)

    // --- Repositories ---
    // Future: singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}

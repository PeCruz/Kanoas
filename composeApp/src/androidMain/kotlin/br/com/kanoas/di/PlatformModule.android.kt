package br.com.kanoas.di

import org.koin.dsl.module

/**
 * Módulo Koin com dependências específicas da plataforma Android.
 * Exemplo: dispatchers, contexto de banco de dados, etc.
 */
fun platformModule() = module {
    // Platform-specific dependencies go here
    // Example: single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
}

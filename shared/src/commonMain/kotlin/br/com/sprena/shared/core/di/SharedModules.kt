package br.com.sprena.shared.core.di

import br.com.sprena.shared.sportclient.di.sportClientModule

/**
 * Agrega todos os módulos Koin do módulo `shared`.
 *
 * Use esta função no `startKoin` da aplicação para registrar
 * toda a infraestrutura e domínios compartilhados.
 *
 * Ordem de carregamento:
 *  1. [kanbanModule]       → domínio Kanban
 *  2. [financialModule]    → domínio Financeiro
 *  3. [sportClientModule]  → domínio SportClient
 *
 * Firebase Firestore é inicializado no platformModule (Android)
 * e injetado via Koin nas implementações de Repository.
 */
fun sharedModules() = listOf(
    kanbanModule,
    financialModule,
    sportClientModule,
)

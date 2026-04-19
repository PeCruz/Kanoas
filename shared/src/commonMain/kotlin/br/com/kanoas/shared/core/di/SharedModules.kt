package br.com.kanoas.shared.core.di

/**
 * Agrega todos os módulos Koin do módulo `shared`.
 *
 * Use esta função no `startKoin` da aplicação para registrar
 * toda a infraestrutura e domínios compartilhados.
 *
 * Ordem de carregamento é importante:
 *  1. [networkModule]   → Supabase client (depende de SupabaseConfig do platformModule)
 *  2. [databaseModule]  → KanoasDatabase (depende de DatabaseDriverFactory do platformModule)
 *  3. [kanbanModule]    → domínio Kanban (depende de network + database)
 *  4. [financialModule] → domínio Financeiro (depende de network + database)
 */
fun sharedModules() = listOf(
    networkModule,
    //databaseModule,
    kanbanModule,
    financialModule,
)

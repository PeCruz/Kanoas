package br.com.kanoas.shared.core.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

/**
 * Cria e configura o cliente Supabase com todos os plugins necessários.
 *
 * Plugins instalados:
 *  - [Postgrest]  → operações CRUD via REST API (PostgreSQL)
 *  - [Auth]       → autenticação (email, magic link, OAuth)
 *  - [Realtime]   → subscriptions em tempo real via WebSocket (ex: Kanban live)
 *  - [Storage]    → upload/download de arquivos (ex: anexos de tarefas)
 *
 * @param config Credenciais do projeto Supabase. Veja [SupabaseConfig].
 */
fun createKanoasSupabaseClient(config: SupabaseConfig) = createSupabaseClient(
    supabaseUrl = config.url,
    supabaseKey = config.anonKey,
) {
    install(Postgrest)
    install(Auth)
    install(Realtime)
    install(Storage)
}

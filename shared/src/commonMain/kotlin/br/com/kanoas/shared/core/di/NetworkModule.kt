package br.com.kanoas.shared.core.di

import br.com.kanoas.shared.core.network.createKanoasSupabaseClient
import org.koin.dsl.module

/**
 * Módulo Koin de infraestrutura de rede.
 *
 * Provê:
 *  - Cliente Supabase configurado (Postgrest, Auth, Realtime, Storage)
 *
 * ⚠️ Requer que [br.com.kanoas.shared.core.network.SupabaseConfig] esteja
 * registrado no Koin antes deste módulo (feito no `platformModule` Android).
 */
val networkModule = module {
    single { createKanoasSupabaseClient(config = get()) }
}

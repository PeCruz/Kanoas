package br.com.kanoas.di

import br.com.kanoas.presentation.core.navigation.BottomNavViewModel
import br.com.kanoas.presentation.core.theme.ThemeViewModel
import br.com.kanoas.presentation.financial.FinancialViewModel
import br.com.kanoas.presentation.financial.addtransaction.AddTransactionViewModel
import br.com.kanoas.presentation.home.HomeViewModel
import br.com.kanoas.presentation.kanban.KanbanViewModel
import br.com.kanoas.presentation.kanban.addtask.AddTaskViewModel
import br.com.kanoas.presentation.kanban.createtask.CreateTaskViewModel
import br.com.kanoas.presentation.bar.BarViewModel
import br.com.kanoas.presentation.bar.addclient.AddClientViewModel
import br.com.kanoas.presentation.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::KanbanViewModel)
    viewModelOf(::AddTaskViewModel)
    viewModel { CreateTaskViewModel() }
    viewModelOf(::FinancialViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::BottomNavViewModel)
    viewModelOf(::ThemeViewModel)
    viewModelOf(::BarViewModel)
    viewModelOf(::AddClientViewModel)
}
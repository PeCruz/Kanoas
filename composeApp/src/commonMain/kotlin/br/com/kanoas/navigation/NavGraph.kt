package br.com.kanoas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.kanoas.presentation.core.navigation.BottomNavIntent
import br.com.kanoas.presentation.core.navigation.BottomNavViewModel
import br.com.kanoas.presentation.core.navigation.BottomTab
import br.com.kanoas.presentation.core.theme.ThemeViewModel
import br.com.kanoas.presentation.financial.FinancialScreen
import br.com.kanoas.presentation.financial.FinancialViewModel
import br.com.kanoas.presentation.financial.addtransaction.AddTransactionDialog
import br.com.kanoas.presentation.financial.addtransaction.AddTransactionViewModel
import br.com.kanoas.presentation.kanban.KanbanIntent
import br.com.kanoas.presentation.kanban.KanbanScreen
import br.com.kanoas.presentation.kanban.KanbanViewModel
import br.com.kanoas.presentation.kanban.createtask.CreateTaskScreen
import br.com.kanoas.presentation.kanban.createtask.CreateTaskViewModel
import br.com.kanoas.presentation.login.LoginScreen
import br.com.kanoas.presentation.login.LoginViewModel
import br.com.kanoas.presentation.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Rotas de navegação do app.
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CREATE_TASK = "create_task"
    const val SETTINGS = "settings"
}

/**
 * Grafo de navegação principal.
 * Login → Home (BottomNav: Home / Financeiro) → CreateTask | Settings.
 */
@Composable
fun NavGraph(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
    ) {
        composable(route = Routes.LOGIN) {
            val loginViewModel: LoginViewModel = koinViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                themeViewModel = themeViewModel,
                onNavigateHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(route = Routes.HOME) { backStackEntry ->
            // Observe task created result from CreateTask screen
            val savedStateHandle = backStackEntry.savedStateHandle
            val createdName = savedStateHandle.get<String>("created_task_name")
            val createdPriority = savedStateHandle.get<Int>("created_task_priority")

            HomeWithBottomNav(
                navController = navController,
                themeViewModel = themeViewModel,
                createdTaskName = createdName,
                createdTaskPriority = createdPriority,
                onTaskConsumed = {
                    savedStateHandle.remove<String>("created_task_name")
                    savedStateHandle.remove<Int>("created_task_priority")
                },
            )
        }

        composable(route = Routes.CREATE_TASK) {
            val createTaskViewModel: CreateTaskViewModel = koinViewModel()
            CreateTaskScreen(
                viewModel = createTaskViewModel,
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateSettings = { navController.navigate(Routes.SETTINGS) },
                onTaskCreated = { name, priority ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("created_task_name", name)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("created_task_priority", priority)
                    navController.popBackStack()
                },
            )
        }

        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

/**
 * Tela principal pós-login com BottomNav (Home / Financeiro).
 */
@Composable
private fun HomeWithBottomNav(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    createdTaskName: String? = null,
    createdTaskPriority: Int? = null,
    onTaskConsumed: () -> Unit = {},
) {
    val bottomNavViewModel: BottomNavViewModel = koinViewModel()
    val bottomNavState by bottomNavViewModel.state.collectAsState()

    // Kanban
    val kanbanViewModel: KanbanViewModel = koinViewModel()

    // Consume created task from CreateTaskScreen
    LaunchedEffect(createdTaskName, createdTaskPriority) {
        if (createdTaskName != null && createdTaskPriority != null) {
            kanbanViewModel.handleIntent(
                KanbanIntent.TaskCreated(createdTaskName, createdTaskPriority),
            )
            onTaskConsumed()
        }
    }

    // Financial
    val financialViewModel: FinancialViewModel = koinViewModel()
    val financialState by financialViewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = bottomNavState.current == BottomTab.KANBAN,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.KANBAN),
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                        )
                    },
                    label = { Text("Home") },
                )
                NavigationBarItem(
                    selected = bottomNavState.current == BottomTab.FINANCIAL,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.FINANCIAL),
                        )
                    },
                    icon = {
                        Text(
                            text = "R$",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    label = { Text("Financeiro") },
                )
            }
        },
    ) { _ ->
        when (bottomNavState.current) {
            BottomTab.KANBAN -> {
                KanbanScreen(
                    viewModel = kanbanViewModel,
                    themeViewModel = themeViewModel,
                    onNavigateCreateTask = {
                        navController.navigate(Routes.CREATE_TASK)
                    },
                    onNavigateSettings = {
                        navController.navigate(Routes.SETTINGS)
                    },
                )
            }

            BottomTab.FINANCIAL -> {
                FinancialScreen(
                    viewModel = financialViewModel,
                    themeViewModel = themeViewModel,
                )
            }
        }
    }

    // --- Dialogs ---

    // AddTransaction Dialog
    if (financialState.isAddDialogVisible) {
        val addTransactionViewModel: AddTransactionViewModel = koinViewModel()
        AddTransactionDialog(
            viewModel = addTransactionViewModel,
            onDismiss = {
                financialViewModel.handleIntent(
                    br.com.kanoas.presentation.financial.FinancialIntent.DismissAddDialog,
                )
            },
            onTransactionCreated = {
                financialViewModel.handleIntent(
                    br.com.kanoas.presentation.financial.FinancialIntent.DismissAddDialog,
                )
            },
        )
    }
}
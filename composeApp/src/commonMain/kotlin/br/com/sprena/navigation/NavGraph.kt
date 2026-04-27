package br.com.sprena.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.sprena.presentation.core.navigation.BottomNavIntent
import br.com.sprena.presentation.core.navigation.BottomNavViewModel
import br.com.sprena.presentation.core.navigation.BottomTab
import br.com.sprena.presentation.core.theme.ThemeViewModel
import br.com.sprena.presentation.bar.BarIntent
import br.com.sprena.presentation.bar.BarScreen
import br.com.sprena.presentation.bar.BarViewModel
import br.com.sprena.presentation.bar.addclient.AddClientDialog
import br.com.sprena.presentation.bar.addclient.AddClientViewModel
import br.com.sprena.presentation.bar.clientdetail.ClientDetailSheet
import br.com.sprena.presentation.bar.clientdetail.ClientDetailViewModel
import br.com.sprena.presentation.financial.FinancialScreen
import br.com.sprena.presentation.financial.FinancialViewModel
import br.com.sprena.presentation.financial.addtransaction.AddTransactionDialog
import br.com.sprena.presentation.financial.addtransaction.AddTransactionViewModel
import br.com.sprena.presentation.kanban.KanbanIntent
import br.com.sprena.presentation.kanban.KanbanScreen
import br.com.sprena.presentation.kanban.KanbanViewModel
import br.com.sprena.core.platform.rememberFilePicker
import br.com.sprena.presentation.kanban.createtask.CreateTaskIntent
import br.com.sprena.presentation.kanban.createtask.CreateTaskScreen
import br.com.sprena.presentation.kanban.createtask.CreateTaskViewModel
import br.com.sprena.presentation.login.LoginScreen
import br.com.sprena.presentation.login.LoginViewModel
import br.com.sprena.presentation.category.CategoryScreen
import br.com.sprena.presentation.category.CategoryViewModel
import br.com.sprena.presentation.menu.MenuItem
import br.com.sprena.presentation.menu.MenuScreen
import br.com.sprena.presentation.menu.MenuViewModel
import br.com.sprena.presentation.settings.SettingsScreen
import br.com.sprena.presentation.sportclient.SportClientScreen
import br.com.sprena.presentation.sportclient.SportClientViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Rotas de navegação do app.
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CREATE_TASK = "create_task"
    const val SETTINGS = "settings"
    const val MENU = "menu"
    const val CATEGORY = "category"
}

/**
 * Grafo de navegação principal.
 * Login → Home (BottomNav: Home / Financeiro) → CreateTask | Settings.
 */
@Composable
fun NavGraph(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    // MenuViewModel shared between Cardápio (Settings) and Comandas (Bar → Itens Consumidos)
    val menuViewModel: MenuViewModel = koinViewModel()
    // CategoryViewModel shared between Settings (manage) and Financial (AddTransaction dropdown)
    val categoryViewModel: CategoryViewModel = koinViewModel()

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
                menuViewModel = menuViewModel,
                categoryViewModel = categoryViewModel,
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

            val launchFilePicker = rememberFilePicker { pickedFile ->
                createTaskViewModel.handleIntent(
                    CreateTaskIntent.AttachmentSelected(
                        name = pickedFile.name,
                        sizeBytes = pickedFile.sizeBytes,
                    ),
                )
            }

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
                onPickFile = launchFilePicker,
            )
        }

        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateMenu = { navController.navigate(Routes.MENU) },
                onNavigateCategory = { navController.navigate(Routes.CATEGORY) },
            )
        }

        composable(route = Routes.MENU) {
            MenuScreen(
                viewModel = menuViewModel,
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(route = Routes.CATEGORY) {
            CategoryScreen(
                viewModel = categoryViewModel,
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

/**
 * Tela principal pós-login com BottomNav (Home / Financeiro / Bar).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeWithBottomNav(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    menuViewModel: MenuViewModel,
    categoryViewModel: CategoryViewModel,
    createdTaskName: String? = null,
    createdTaskPriority: Int? = null,
    onTaskConsumed: () -> Unit = {},
) {
    val bottomNavViewModel: BottomNavViewModel = koinViewModel()
    val bottomNavState by bottomNavViewModel.state.collectAsState()

    // Sport Clients (Home)
    val sportClientViewModel: SportClientViewModel = koinViewModel()

    // Kanban (Quadro)
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

    // Bar
    val barViewModel: BarViewModel = koinViewModel()
    val barState by barViewModel.state.collectAsState()

    // Menu items (shared with Cardápio)
    val menuState by menuViewModel.state.collectAsState()

    // Categories (shared with Financial)
    val categoryState by categoryViewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = bottomNavState.current == BottomTab.HOME,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.HOME),
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
                    selected = bottomNavState.current == BottomTab.QUADRO,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.QUADRO),
                        )
                    },
                    icon = {
                        Text(
                            text = "\uD83D\uDCCB",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    label = { Text("Quadro") },
                )
                NavigationBarItem(
                    selected = bottomNavState.current == BottomTab.BAR,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.BAR),
                        )
                    },
                    icon = {
                        Text(
                            text = "\uD83C\uDF7A",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    label = { Text("Comandas") },
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
                NavigationBarItem(
                    selected = bottomNavState.current == BottomTab.SETTINGS,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.SETTINGS),
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                        )
                    },
                    label = { Text("Config") },
                )
            }
        },
    ) { bottomNavPadding ->
        when (bottomNavState.current) {
            BottomTab.HOME -> {
                SportClientScreen(
                    viewModel = sportClientViewModel,
                    themeViewModel = themeViewModel,
                    modifier = Modifier.padding(bottomNavPadding),
                )
            }

            BottomTab.QUADRO -> {
                KanbanScreen(
                    viewModel = kanbanViewModel,
                    themeViewModel = themeViewModel,
                    modifier = Modifier.padding(bottomNavPadding),
                    onNavigateCreateTask = {
                        navController.navigate(Routes.CREATE_TASK)
                    },
                )
            }

            BottomTab.FINANCIAL -> {
                FinancialScreen(
                    viewModel = financialViewModel,
                    themeViewModel = themeViewModel,
                    modifier = Modifier.padding(bottomNavPadding),
                )
            }

            BottomTab.BAR -> {
                BarScreen(
                    viewModel = barViewModel,
                    themeViewModel = themeViewModel,
                    modifier = Modifier.padding(bottomNavPadding),
                )
            }

            BottomTab.SETTINGS -> {
                SettingsScreen(
                    themeViewModel = themeViewModel,
                    modifier = Modifier.padding(bottomNavPadding),
                    onNavigateMenu = {
                        navController.navigate(Routes.MENU)
                    },
                    onNavigateCategory = {
                        navController.navigate(Routes.CATEGORY)
                    },
                )
            }
        }
    }

    // --- Dialogs ---

    // AddTransaction Dialog — fresh VM each time (no Koin cache)
    if (financialState.isAddDialogVisible) {
        val addTransactionViewModel = remember { AddTransactionViewModel() }
        AddTransactionDialog(
            viewModel = addTransactionViewModel,
            categories = categoryState.categories,
            onDismiss = {
                financialViewModel.handleIntent(
                    br.com.sprena.presentation.financial.FinancialIntent.DismissAddDialog,
                )
            },
            onTransactionCreated = { transaction ->
                financialViewModel.handleIntent(
                    br.com.sprena.presentation.financial.FinancialIntent.TransactionAdded(transaction),
                )
            },
        )
    }

    // EditTransaction Dialog — fresh VM pre-filled with existing data
    if (financialState.isEditDialogVisible && financialState.editingTransactionId != null) {
        val editingTx = financialState.transactions.find {
            it.id == financialState.editingTransactionId
        }
        if (editingTx != null) {
            val editTransactionViewModel = remember(editingTx.id) {
                AddTransactionViewModel()
            }
            LaunchedEffect(editingTx.id) {
                editTransactionViewModel.handleIntent(
                    br.com.sprena.presentation.financial.addtransaction.AddTransactionIntent.LoadForEdit(editingTx),
                )
            }
            AddTransactionDialog(
                viewModel = editTransactionViewModel,
                categories = categoryState.categories,
                isEditMode = true,
                onDismiss = {
                    financialViewModel.handleIntent(
                        br.com.sprena.presentation.financial.FinancialIntent.DismissEditDialog,
                    )
                },
                onTransactionCreated = { /* not used in edit mode */ },
                onTransactionUpdated = { transaction ->
                    financialViewModel.handleIntent(
                        br.com.sprena.presentation.financial.FinancialIntent.TransactionUpdated(transaction),
                    )
                },
                onTransactionDeleted = {
                    financialViewModel.handleIntent(
                        br.com.sprena.presentation.financial.FinancialIntent.TransactionDeleted(editingTx.id),
                    )
                },
            )
        }
    }

    // AddClient Dialog — cria VM novo cada vez que o diálogo abre (sem cache)
    if (barState.isAddClientDialogVisible) {
        val addClientViewModel = remember { AddClientViewModel() }
        AddClientDialog(
            viewModel = addClientViewModel,
            onDismiss = {
                barViewModel.handleIntent(BarIntent.DismissAddClientDialog)
            },
            onClientCreated = { client ->
                barViewModel.handleIntent(BarIntent.ClientAdded(client))
                barViewModel.handleIntent(BarIntent.DismissAddClientDialog)
            },
        )
    }

    // ClientDetail BottomSheet
    if (barState.selectedClient != null) {
        val selectedClient = barState.selectedClient!!
        val clientDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val clientDetailViewModel = remember(selectedClient.id) {
            ClientDetailViewModel(client = selectedClient)
        }
        ClientDetailSheet(
            viewModel = clientDetailViewModel,
            sheetState = clientDetailSheetState,
            menuItems = menuState.items,
            onDismiss = {
                barViewModel.handleIntent(BarIntent.DismissClientDetail)
            },
            onClientUpdated = { updatedClient ->
                barViewModel.handleIntent(BarIntent.ClientUpdated(updatedClient))
            },
            onClientDeleted = { clientId ->
                barViewModel.handleIntent(BarIntent.ClientDeleted(clientId))
                barViewModel.handleIntent(BarIntent.DismissClientDetail)
            },
        )
    }
}
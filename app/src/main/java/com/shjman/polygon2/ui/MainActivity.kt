package com.shjman.polygon2.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.shjman.polygon2.R
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.convertDateStringToLocalDateTime
import com.shjman.polygon2.ui.MainActivity.Companion.KEY_SHARED_DOCUMENT_PATH
import com.shjman.polygon2.ui.MainActivity.Companion.SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED
import com.shjman.polygon2.ui.categories.CategoriesScreen
import com.shjman.polygon2.ui.categories.CategoriesViewModel
import com.shjman.polygon2.ui.categories.EditCategoryScreen
import com.shjman.polygon2.ui.categories.EditCategoryViewModel
import com.shjman.polygon2.ui.edit_spending.EditSpendingScreen
import com.shjman.polygon2.ui.edit_spending.EditSpendingViewModel
import com.shjman.polygon2.ui.home.HomeScreen
import com.shjman.polygon2.ui.home.HomeViewModel
import com.shjman.polygon2.ui.settings.*
import com.shjman.polygon2.ui.theme.Polygon2Theme
import com.shjman.polygon2.ui.unauthorized.UnauthorizedScreen
import com.shjman.polygon2.ui.unauthorized.UnauthorizedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val addTrustedUserViewModel: AddTrustedUserViewModel by viewModel()
    private val categoriesViewModel: CategoriesViewModel by viewModel()
    private val editCategoryViewModel: EditCategoryViewModel by viewModel()
    private val editSpendingViewModel: EditSpendingViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val spentViewModel: SpentViewModel by viewModel()
    private val unauthorizedViewModel: UnauthorizedViewModel by viewModel()

    private var scope: CoroutineScope? = null
    private val showSnackbarSharedFlow = MutableSharedFlow<String>()

    private val loginLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        scope?.launch {
            if (it.resultCode == RESULT_OK) {
                unauthorizedViewModel.updateDataAfterSuccessSignIn()
                Timber.d("FirebaseAuthUIAuthenticationResult == RESULT_OK")
            } else {
                Timber.d("FirebaseAuthUIAuthenticationResult == idpResponse?.error == ${it.idpResponse?.error}")
            }
            unauthorizedViewModel.checkIsUserSignIn()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.data?.getQueryParameter(KEY_SHARED_DOCUMENT_PATH)?.let {
            mainViewModel.saveSharedDocumentPath(it)
        }
        setContent {
            val scaffoldState = rememberScaffoldState()
            scope = rememberCoroutineScope()
            LaunchedEffect(Unit) {
                scope?.let {
                    showSnackbarSharedFlow
                        .distinctUntilChanged()
                        .onEach { message ->
                            showSnackbar(
                                message = message,
                                scaffoldState = scaffoldState,
                                scope = it,
                            )
                        }.launchIn(it)
                }
            }
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isShowingBottomBar = rememberSaveable { (mutableStateOf(true)) }
            Polygon2Theme {
                setupBottomBarVisibility(currentRoute, isShowingBottomBar)
                Scaffold(
                    bottomBar = { AnimatedBottomNavigation(navController, currentRoute, isShowingBottomBar) },
                    scaffoldState = scaffoldState,
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavigationGraph(
                            addTrustedUserViewModel = addTrustedUserViewModel,
                            categoriesViewModel = categoriesViewModel,
                            context = this@MainActivity,
                            editCategoryViewModel = editCategoryViewModel,
                            editSpendingViewModel = editSpendingViewModel,
                            homeViewModel = homeViewModel,
                            loginLauncher = loginLauncher,
                            navHostController = navController,
                            scaffoldState = scaffoldState,
                            showSnackbarMutableSharedFlow = showSnackbarSharedFlow,
                            spentViewModel = spentViewModel,
                            unauthorizedViewModel = unauthorizedViewModel,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun setupBottomBarVisibility(currentRoute: String?, isShowingBottomBar: MutableState<Boolean>) {
        when (currentRoute) {
            Screens.BottomNavItem.Setting.screenRoute,
            Screens.BottomNavItem.Home.screenRoute,
            Screens.BottomNavItem.Spent.screenRoute,
            Screens.BottomNavItem.Overview.screenRoute -> {
                isShowingBottomBar.value = true
            }
            Screens.Categories.screenRoute,
            Screens.EditCategory.screenRoute,
            Screens.EditSpending.screenRoute -> {
                isShowingBottomBar.value = false
            }
            else -> {
                isShowingBottomBar.value = false
            }
        }
    }

    companion object {
        const val SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED = 350
        const val KEY_SHARED_DOCUMENT_PATH = "dp"
    }
}

@Composable
fun AnimatedBottomNavigation(
    navController: NavController,
    currentRoute: String?,
    isShowingBottomBar: MutableState<Boolean>,
) {
    AnimatedVisibility(
        visible = isShowingBottomBar.value,
        enter = fadeIn(
            animationSpec = tween(durationMillis = SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED),
        ) + expandVertically(
            animationSpec = tween(durationMillis = SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED),
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED),
        ) + shrinkVertically(
            animationSpec = tween(durationMillis = SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED),
        ),
        content = { BottomNavigation(navController, currentRoute) },
    )
}

@Composable
fun BottomNavigation(
    navController: NavController,
    currentRoute: String?,
) {
    val bottomItems = listOf(
        Screens.BottomNavItem.Home,
        Screens.BottomNavItem.Spent,
        Screens.BottomNavItem.Overview,
        Screens.BottomNavItem.Setting
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        bottomItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp
                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        popUpTo(Screens.BottomNavItem.Home.screenRoute) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(
    addTrustedUserViewModel: AddTrustedUserViewModel,
    categoriesViewModel: CategoriesViewModel,
    context: Context,
    editCategoryViewModel: EditCategoryViewModel,
    editSpendingViewModel: EditSpendingViewModel,
    homeViewModel: HomeViewModel,
    loginLauncher: ActivityResultLauncher<Intent>,
    navHostController: NavHostController,
    scaffoldState: ScaffoldState,
    showSnackbarMutableSharedFlow: MutableSharedFlow<String>,
    spentViewModel: SpentViewModel,
    unauthorizedViewModel: UnauthorizedViewModel,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.Unauthorized.screenRoute,
    ) {
        composable(Screens.BottomNavItem.Home.screenRoute) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onClickGoNext = { navHostController.navigate(Screens.BottomNavItem.Spent.screenRoute) },
            )
        }
        composable(Screens.BottomNavItem.Spent.screenRoute) {
            SpentScreen(
                showSnackbarMutableSharedFlow = showSnackbarMutableSharedFlow,
                spentViewModel = spentViewModel,
            )
        }
        composable(Screens.BottomNavItem.Overview.screenRoute) {
            OverviewScreen(
                spentViewModel = spentViewModel,
                onEditSpendingClicked = { localDateTime -> // todo rework to use id of spending
                    val localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
                    navHostController.navigate(Screens.EditSpending.screenRoute + "/$localDateTimeString") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(Screens.BottomNavItem.Setting.screenRoute) {
            SettingScreen(
                navigateToCategoriesScreen = {
                    navHostController.navigate(Screens.Categories.screenRoute)
                },
                navigateToSharingSettingsScreen = {
                    navHostController.navigate(Screens.SharingSettings.screenRoute)
                },
                navigateToUnauthorizedScreen = {
                    navHostController.navigate(Screens.Unauthorized.screenRoute) {
                        popUpTo(Screens.BottomNavItem.Home.screenRoute) {
                            inclusive = true
                        }
                    }
                },
                showSnackbarMutableSharedFlow = showSnackbarMutableSharedFlow,
            )
        }
        composable(
            route = Screens.AddTrustedUserScreen.screenRoute
        ) {
            AddTrustedUserScreen(
                addTrustedUserViewModel = addTrustedUserViewModel,
                popBackStack = { navHostController.popBackStack() },
                showSnackbarMutableSharedFlow = showSnackbarMutableSharedFlow,
            )
        }
        composable(
            route = Screens.Categories.screenRoute
        ) {
            CategoriesScreen(
                categoriesViewModel = categoriesViewModel,
                navigateToEditCategory = { navHostController.navigate(Screens.EditCategory.screenRoute) },
                showSnackbarMutableSharedFlow = showSnackbarMutableSharedFlow,
            )
        }
        composable(
            route = Screens.EditCategory.screenRoute
        ) {
            EditCategoryScreen(
                editCategoryViewModel = editCategoryViewModel,
                popBackStack = { navHostController.popBackStack() },
                showSnackbarMutableSharedFlow = showSnackbarMutableSharedFlow,
            )
        }
        composable(
            route = Screens.EditSpending.screenRoute + "/{localDateTimeString}",
            arguments = listOf(navArgument("localDateTimeString") { type = NavType.StringType })
        )
        { backStackEntry ->
            val localDateTimeString = backStackEntry.arguments?.getString("localDateTimeString")
            localDateTimeString?.let {
                EditSpendingScreen(
                    context = context,
                    editSpendingViewModel = editSpendingViewModel,
                    localDateTimeSpending = convertDateStringToLocalDateTime(it),
                    navigatePopBackClicked = { navHostController.popBackStack() },
                    scaffoldState = scaffoldState,
                    showSnackbarMutableSharedFlow = showSnackbarMutableSharedFlow,
                )
            }
        }
        composable(
            route = Screens.SharingSettings.screenRoute
        ) {
            SharingSettingsScreen(
                navigateToAddTrustedUser = { navHostController.navigate(Screens.AddTrustedUserScreen.screenRoute) },
                sendInviteLink = { documentPath ->
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "https://shjman/spendings?$KEY_SHARED_DOCUMENT_PATH=$documentPath")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "send invite link of your database"))
                },
                showSnackbarMutableSharedFlow = showSnackbarMutableSharedFlow,
            )
        }
        composable(
            route = Screens.Unauthorized.screenRoute
        ) {
            UnauthorizedScreen(
                loginLauncher = loginLauncher,
                unauthorizedViewModel = unauthorizedViewModel,
                navigateToHomeScreen = {
                    navHostController.navigate(Screens.BottomNavItem.Home.screenRoute) {
                        navHostController.graph.startDestinationRoute?.let { startDestinationRoute ->
                            popUpTo(startDestinationRoute) {
                                inclusive = true
                            }
                        }
                    }
                },
            )
        }
    }

}

fun showSnackbar(
    message: String,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
) {
    scope.launch {
        val snackbarHostState = scaffoldState.snackbarHostState
        val snackbarResult = snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Long,
            actionLabel = "got it"
        )
        if (snackbarResult == SnackbarResult.ActionPerformed) {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
}

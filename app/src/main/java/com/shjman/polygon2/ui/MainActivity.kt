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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.shjman.polygon2.ui.MainActivity.Companion.SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED
import com.shjman.polygon2.ui.categories.CategoriesScreen
import com.shjman.polygon2.ui.categories.CategoriesViewModel
import com.shjman.polygon2.ui.categories.EditCategoryScreen
import com.shjman.polygon2.ui.categories.EditCategoryViewModel
import com.shjman.polygon2.ui.settings.*
import com.shjman.polygon2.ui.theme.Polygon2Theme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val addTrustedUserViewModel: AddTrustedUserViewModel by viewModel()
    private val categoriesViewModel: CategoriesViewModel by viewModel()
    private val editCategoryViewModel: EditCategoryViewModel by viewModel()
    private val editSpendingViewModel: EditSpendingViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val settingViewModel: SettingViewModel by viewModel()
    private val sharingSettingViewModel: SharingSettingViewModel by viewModel()
    private val spentViewModel: SpentViewModel by viewModel()

    private val loginLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        homeViewModel.showSnackBar(if (it.resultCode == RESULT_OK) "login success" else "login error")
        homeViewModel.checkIsUserLoggedIn()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
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
                            settingViewModel = settingViewModel,
                            sharingSettingViewModel = sharingSettingViewModel,
                            spentViewModel = spentViewModel,
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
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
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
    settingViewModel: SettingViewModel,
    sharingSettingViewModel: SharingSettingViewModel,
    spentViewModel: SpentViewModel,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.BottomNavItem.Home.screenRoute,
    ) {
        composable(Screens.BottomNavItem.Home.screenRoute) {
            HomeScreen(
                context = context,
                homeViewModel = homeViewModel,
                loginLauncher = loginLauncher,
                scaffoldState = scaffoldState,
                onClickGoNext = {
                    navHostController.navigate(Screens.BottomNavItem.Spent.screenRoute) {
                        navHostController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(Screens.BottomNavItem.Spent.screenRoute) {
            SpentScreen(
                spentViewModel = spentViewModel,
                onNavigateToCategoriesScreenClicked = { navHostController.navigate(Screens.Categories.screenRoute) },
            )
        }
        composable(Screens.BottomNavItem.Overview.screenRoute) {
            OverviewScreen(
                spentViewModel = spentViewModel,
                onEditSpendingClicked = { localDateTime ->
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
                settingViewModel = settingViewModel,
                navHostController = navHostController,
                onSharingSpendingsClicked = {},
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
                    localDateTimeSpending = convertDateStringToLocalDateTime(it),
                    editSpendingViewModel = editSpendingViewModel,
                    context = context,
                    scaffoldState = scaffoldState,
                    navigatePopBackClicked = { navHostController.popBackStack() },
                )
            }
        }
        composable(
            route = Screens.EditCategory.screenRoute
        ) {
            EditCategoryScreen(
                editCategoryViewModel = editCategoryViewModel,
                popBackStack = { navHostController.popBackStack() },
            )
        }
        composable(
            route = Screens.Categories.screenRoute
        ) {
            CategoriesScreen(
                categoriesViewModel = categoriesViewModel,
                navigateToEditCategory = { navHostController.navigate(Screens.EditCategory.screenRoute) },
            )
        }
        composable(
            route = Screens.SharingSettings.screenRoute
        ) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is my text to send some deeplink.") // todo here should be deeplink
                type = "text/plain"
            }
            SharingSettingsScreen(
                sharingSettingViewModel = sharingSettingViewModel,
                navigateToAddTrustedUser = { navHostController.navigate(Screens.AddTrustedUserScreen.screenRoute) },
                sendInviteLink = { context.startActivity(Intent.createChooser(sendIntent, "send somebody it")) },
            )
        }
        composable(
            route = Screens.AddTrustedUserScreen.screenRoute
        ) {
            AddTrustedUserScreen(
                addTrustedUserViewModel = addTrustedUserViewModel,
                popBackStack = { navHostController.popBackStack() },
            )
        }
    }
}

package com.shjman.polygon2.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.shjman.polygon2.R
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.convertDateStringToLocalDateTime
import com.shjman.polygon2.ui.MainActivity.Companion.SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED
import com.shjman.polygon2.ui.categories.CategoriesScreen
import com.shjman.polygon2.ui.categories.CategoriesViewModel
import com.shjman.polygon2.ui.categories.EditCategoryScreen
import com.shjman.polygon2.ui.categories.EditCategoryViewModel
import com.shjman.polygon2.ui.theme.Polygon2Theme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val spentViewModel: SpentViewModel by viewModel()
    private val editSpendingViewModel: EditSpendingViewModel by viewModel()
    private val categoriesViewModel: CategoriesViewModel by viewModel()
    private val editCategoryViewModel: EditCategoryViewModel by viewModel()

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
                ) {
                    Box {
                        NavigationGraph(
                            navHostController = navController,
                            spentViewModel = spentViewModel,
                            editSpendingViewModel = editSpendingViewModel,
                            categoriesViewModel = categoriesViewModel,
                            editCategoryViewModel = editCategoryViewModel,
                            context = this@MainActivity,
                            scaffoldState = scaffoldState,
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
            Screens.EditSpending.screenRoute -> {
                isShowingBottomBar.value = false
            }
            Screens.Categories.screenRoute -> {
                isShowingBottomBar.value = false
            }
            Screens.EditCategory.screenRoute -> {
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
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED),
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
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
    navHostController: NavHostController,
    spentViewModel: SpentViewModel,
    editSpendingViewModel: EditSpendingViewModel,
    categoriesViewModel: CategoriesViewModel,
    editCategoryViewModel: EditCategoryViewModel,
    context: Context,
    scaffoldState: ScaffoldState,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.BottomNavItem.Home.screenRoute,
    ) {
        composable(Screens.BottomNavItem.Home.screenRoute) {
            HomeScreen(
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
            SettingScreen()
        }
        composable(
            route = Screens.EditSpending.screenRoute + "/{localDateTimeString}",
            arguments = listOf(navArgument("localDateTimeString") { type = NavType.StringType })
        )
        { backStackEntry ->
            val localDateTimeString = backStackEntry.arguments?.getString("localDateTimeString")
            localDateTimeString?.let {
                EditSpendingScreen(
                    localDateTime = convertDateStringToLocalDateTime(it),
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
    }
}

package com.shjman.polygon2.ui

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shjman.polygon2.R
import com.shjman.polygon2.ui.MainActivity.Companion.SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED
import com.shjman.polygon2.ui.theme.Polygon2Theme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val spentViewModel: SpentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isShowingBottomBar = rememberSaveable { (mutableStateOf(true)) }
            Polygon2Theme {
                setupBottomBarVisibility(currentRoute, isShowingBottomBar)
                Scaffold(
                    bottomBar = { AnimatedBottomNavigation(navController, currentRoute, isShowingBottomBar) },
                ) {
                    Box {
                        NavigationGraph(
                            navController = navController,
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
            Screens.EditSpending.screenRoute -> {
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
    navController: NavHostController,
    spentViewModel: SpentViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.BottomNavItem.Home.screenRoute,
    ) {
        composable(Screens.BottomNavItem.Home.screenRoute) {
            HomeScreen(
                onClickGoNext = {
                    navController.navigate(Screens.BottomNavItem.Spent.screenRoute) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
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
            SpentScreen(spentViewModel = spentViewModel)
        }
        composable(Screens.BottomNavItem.Overview.screenRoute) {
            OverviewScreen(
                spentViewModel = spentViewModel,
                onEditSpendingClicked = { navController.navigate(Screens.EditSpending.screenRoute) },
            )
        }
        composable(Screens.BottomNavItem.Setting.screenRoute) {
            SettingScreen()
        }
        composable(Screens.EditSpending.screenRoute) {
            EditSpendingScreen()
        }
    }
}

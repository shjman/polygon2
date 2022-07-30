package com.shjman.polygon2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shjman.polygon2.ui.theme.Polygon2Theme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val spentViewModel: SpentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Polygon2Theme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigation(navController) }
                ) {
                    NavigationGraph(
                        navController = navController,
                        modifier = Modifier,
                        lifecycleScope,
                        spentViewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val bottomItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Spent,
        BottomNavItem.Overview,
        BottomNavItem.Setting
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        bottomItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
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
    modifier: Modifier,
    lifecycleScope: LifecycleCoroutineScope,
    spentViewModel: SpentViewModel
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.screenRoute,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.screenRoute) {
            HomeScreen(
                onClickGoNext = {
                    navController.navigate(BottomNavItem.Spent.screenRoute) {
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
        composable(BottomNavItem.Spent.screenRoute) {
            SpentScreen(lifecycleScope = lifecycleScope, spentViewModel = spentViewModel)
        }
        composable(BottomNavItem.Overview.screenRoute) {
            OverviewScreen(lifecycleScope = lifecycleScope, spentViewModel = spentViewModel)
        }
        composable(BottomNavItem.Setting.screenRoute) {
            SettingScreen()
        }
    }
}

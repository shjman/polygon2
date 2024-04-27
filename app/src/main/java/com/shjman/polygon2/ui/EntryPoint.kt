package com.shjman.polygon2.ui

import android.content.Intent
import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.shjman.polygon2.R
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.convertDateStringToLocalDateTime
import com.shjman.polygon2.ui.categories.CategoriesScreen
import com.shjman.polygon2.ui.categories.EditCategoryScreen
import com.shjman.polygon2.ui.edit_spending.EditSpendingScreen
import com.shjman.polygon2.ui.home.HomeScreen
import com.shjman.polygon2.ui.overview.OverviewScreen
import com.shjman.polygon2.ui.planned.PlannedScreen
import com.shjman.polygon2.ui.settings.AddTrustedUserScreen
import com.shjman.polygon2.ui.settings.SettingScreen
import com.shjman.polygon2.ui.settings.SharingSettingsScreen
import com.shjman.polygon2.ui.snackbar.SnackbarManager
import com.shjman.polygon2.ui.spent.SpentScreen
import com.shjman.polygon2.ui.theme.Polygon2Theme
import com.shjman.polygon2.ui.unauthorized.UnauthorizedScreen
import kotlinx.coroutines.CoroutineScope
import java.time.format.DateTimeFormatter

@Composable
fun EntryPoint(
    entryIntent: Intent,
) {
//    val entryPointViewModel: EntryPointViewModel = koinViewModel() todo

    Polygon2Theme {
        Surface(color = MaterialTheme.colors.background) {
            val appState = rememberAppState()
            val navBackStackEntry by appState.navHostController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isShowingBottomBar by remember(currentRoute) {
                derivedStateOf {
                    Screens.BottomNavItem.values()
                        .map { it.screenRoute }
                        .contains(currentRoute)
                }
            }
            Scaffold(
                bottomBar = { AnimatedBottomNavigation(appState.navHostController, currentRoute, isShowingBottomBar) },
                scaffoldState = appState.scaffoldState,
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    NavigationGraph(
                        appState = appState,
                        entryIntent = entryIntent,
                    )
                }
            }
        }
    }
}

@Composable
fun rememberAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navHostController: NavHostController = rememberNavController(),
    resources: Resources = resources(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    snackbarManager: SnackbarManager = SnackbarManager,
): AppState {
    return remember(coroutineScope, resources, scaffoldState, snackbarManager) {
        AppState(
            coroutineScope = coroutineScope,
            navHostController = navHostController,
            resources = resources,
            scaffoldState = scaffoldState,
            snackbarManager = snackbarManager,
        )
    }
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    return LocalContext.current.resources
}

const val SHOW_HIDE_BOTTOM_BAR_ANIMATION_SPEED = 350
const val KEY_SHARED_DOCUMENT_PATH = "dp"

@Composable
fun AnimatedBottomNavigation(
    navController: NavController,
    currentRoute: String?,
    isShowingBottomBar: Boolean,
) {
    AnimatedVisibility(
        visible = isShowingBottomBar,
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
internal fun BottomNavigation(
    navController: NavController,
    currentRoute: String?,
) {
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        Screens.BottomNavItem.values().forEach { item ->
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
    appState: AppState,
    entryIntent: Intent,
) {
    NavHost(
        navController = appState.navHostController,
        startDestination = Screens.Unauthorized.screenRoute,
    ) {
        composable(Screens.BottomNavItem.Home.screenRoute) {
            HomeScreen(
                onClickGoNext = { appState.navHostController.navigate(Screens.BottomNavItem.Spent.screenRoute) },
            )
        }
        composable(Screens.BottomNavItem.Spent.screenRoute) {
            SpentScreen(
            )
        }
        composable(Screens.BottomNavItem.Overview.screenRoute) {
            OverviewScreen(
                onEditSpendingClicked = { localDateTime -> // todo rework to use id of spending
                    val localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
                    appState.navHostController.navigate(Screens.EditSpending.screenRoute + "/$localDateTimeString") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(Screens.BottomNavItem.Planned.screenRoute) {
            PlannedScreen()
        }
        composable(Screens.BottomNavItem.Setting.screenRoute) {
            SettingScreen(
                navigateToCategoriesScreen = {
                    appState.navHostController.navigate(Screens.Categories.screenRoute)
                },
                navigateToSharingSettingsScreen = {
                    appState.navHostController.navigate(Screens.SharingSettings.screenRoute)
                },
                navigateToUnauthorizedScreen = {
                    appState.navHostController.navigate(Screens.Unauthorized.screenRoute) {
                        popUpTo(Screens.BottomNavItem.Home.screenRoute) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(
            route = Screens.AddTrustedUserScreen.screenRoute
        ) {
            AddTrustedUserScreen(
                popBackStack = { appState.navHostController.popBackStack() },
            )
        }
        composable(
            route = Screens.Categories.screenRoute
        ) {
            CategoriesScreen(
                navigateToEditCategory = { appState.navHostController.navigate(Screens.EditCategory.screenRoute) },
            )
        }
        composable(
            route = Screens.EditCategory.screenRoute
        ) {
            EditCategoryScreen(
                popBackStack = { appState.navHostController.popBackStack() },
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
                    appState = appState,
                    localDateTimeSpending = convertDateStringToLocalDateTime(it),
                )
            }
        }
        composable(
            route = Screens.SharingSettings.screenRoute
        ) {
            SharingSettingsScreen(
                navigateToAddTrustedUser = { appState.navHostController.navigate(Screens.AddTrustedUserScreen.screenRoute) },
            )
        }
        composable(
            route = Screens.Unauthorized.screenRoute
        ) {
            UnauthorizedScreen(
                entryIntent = entryIntent,
                navigateToHomeScreen = {
                    appState.navHostController.navigate(Screens.BottomNavItem.Home.screenRoute) {
                        appState.navHostController.graph.startDestinationRoute?.let { startDestinationRoute ->
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

package com.shjman.polygon2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shjman.polygon2.MyScreens.*
import com.shjman.polygon2.ui.theme.Polygon2Theme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val spentViewModel: SpentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Polygon2Theme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainNavHost(navController, Modifier, lifecycleScope, spentViewModel)
                }
            }
        }
    }
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier,
    lifecycleScope: LifecycleCoroutineScope,
    spentViewModel: SpentViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Home.name,
        modifier = modifier
    ) {
        composable(Home.name) {
            HomeBody(
                onClickGoNext = { navController.navigate(Spent.name) },
            )
        }
        composable(Spent.name) {
            SpentBody(lifecycleScope = lifecycleScope, spentViewModel = spentViewModel)
        }
        composable(Review.name) {
            ReviewBody()
        }
    }
}

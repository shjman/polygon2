package com.shjman.polygon2.ui

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    context: Context,
    homeViewModel: HomeViewModel,
    loginLauncher: ActivityResultLauncher<Intent>,
    onClickGoNext: () -> Unit,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope = rememberCoroutineScope(),
    isUserLoggedIn: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    LaunchedEffect(Unit) {
        homeViewModel.checkIsUserLoggedIn()
        homeViewModel.showSnackbar
            .onEach { showSnackBar(it, scope, scaffoldState) }
            .launchIn(scope)
        homeViewModel.isUserLoggedIn
            .onEach { isUserLoggedIn.value = it }
            .launchIn(scope)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "home screen",
                color = Color.Green,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isUserLoggedIn.value) {
                Button(
                    onClick = {
                        requestToLogout(
                            context = context,
                            homeViewModel = homeViewModel,
                            scaffoldState = scaffoldState,
                            scope = scope,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(text = "logout", color = Color.Black)
                }
                Button(
                    onClick = onClickGoNext,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(text = "go spent screen", color = Color.Black)
                }
            } else {
                Button(
                    onClick = { requestToLogin(loginLauncher) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(text = "login", color = Color.Black)
                }
            }
        }
    }
}

private fun requestToLogin(loginLauncher: ActivityResultLauncher<Intent>) {
    loginLauncher.launch(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
            .build()
    )
}

private fun requestToLogout(
    context: Context,
    homeViewModel: HomeViewModel,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
) = AuthUI.getInstance()
    .signOut(context)
    .addOnCompleteListener {
        homeViewModel.isUserLoggedIn(false)
        showSnackBar("logout success", scope, scaffoldState)
    }
    .addOnFailureListener {
        showSnackBar("logout error", scope, scaffoldState)
        homeViewModel.checkIsUserLoggedIn()
    }


private fun showSnackBar(message: String?, scope: CoroutineScope, scaffoldState: ScaffoldState) {
    message?.let { scope.launch { scaffoldState.snackbarHostState.showSnackbar(it) } }
}
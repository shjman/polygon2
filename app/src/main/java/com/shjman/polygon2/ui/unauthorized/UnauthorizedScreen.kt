package com.shjman.polygon2.ui.unauthorized

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
fun UnauthorizedScreen(
    isLoading: MutableState<Boolean> = remember { mutableStateOf(true) },
    isUserLoggedIn: MutableState<Boolean?> = remember { mutableStateOf(null) },
    loginLauncher: ActivityResultLauncher<Intent>,
    navigateToHomeScreen: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    unauthorizedViewModel: UnauthorizedViewModel
) {
    LaunchedEffect(Unit) {
        unauthorizedViewModel.checkIsUserSignIn()
        unauthorizedViewModel.isUserLoggedIn
            .onEach {
                when (it) {
                    true -> navigateToHomeScreen()
                    false, null -> isUserLoggedIn.value = it
                }
            }
            .launchIn(scope)
        unauthorizedViewModel.isLoading
            .onEach { isLoading.value = it }
            .launchIn(scope)
        unauthorizedViewModel.requestToSignIn
            .onEach { requestToSignIn(loginLauncher) }
            .launchIn(scope)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(
                color = Color.Green,
            )
        } else {
            if (isUserLoggedIn.value == false) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "please sing in to start"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    unauthorizedViewModel.onSignInClicked()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                        ) {
                            Text(text = "sign in", color = Color.Black)
                        }
                    }
                }
            } else {
// white screen and navigation to home screen
            }
        }
    }
}

private fun requestToSignIn(loginLauncher: ActivityResultLauncher<Intent>) {
    loginLauncher.launch(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
            .build()
    )
}
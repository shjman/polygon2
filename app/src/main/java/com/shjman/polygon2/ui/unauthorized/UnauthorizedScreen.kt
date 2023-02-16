package com.shjman.polygon2.ui.unauthorized

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.shjman.polygon2.ui.KEY_SHARED_DOCUMENT_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun UnauthorizedScreen(
    isLoading: MutableState<Boolean> = remember { mutableStateOf(true) },
    isUserLoggedIn: MutableState<Boolean?> = remember { mutableStateOf(null) },
    entryIntent: Intent,
    navigateToHomeScreen: () -> Unit,
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val viewModel = koinViewModel<UnauthorizedViewModel>()
    val signInLauncher = rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) {
        scope.launch {
            if (it.resultCode == ComponentActivity.RESULT_OK) {
                viewModel.updateDataAfterSuccessSignIn()
                Timber.d("FirebaseAuthUIAuthenticationResult == RESULT_OK")
            } else {
                Timber.e("FirebaseAuthUIAuthenticationResult == idpResponse?.error == ${it.idpResponse?.error}")
            }
            viewModel.checkIsUserSignIn()
        }
    }

    LaunchedEffect(Unit) {
        entryIntent.data?.getQueryParameter(KEY_SHARED_DOCUMENT_PATH)?.let {
            viewModel.updateSharedDocumentPath(it)
        }
        viewModel.clearInitState()
        viewModel.checkIsUserSignIn()
        viewModel.isUserLoggedIn
            .onEach {
                when (it) {
                    true -> navigateToHomeScreen()
                    false, null -> isUserLoggedIn.value = it
                }
            }
            .launchIn(scope)
        viewModel.isLoading
            .onEach { isLoading.value = it }
            .launchIn(scope)
        viewModel.requestToSignIn
            .onEach { requestToSignIn(signInLauncher) }
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
                                    viewModel.onSignInClicked()
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
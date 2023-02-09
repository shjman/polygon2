package com.shjman.polygon2.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun SettingScreen(
    navigateToCategoriesScreen: () -> Unit,
    navigateToSharingSettingsScreen: () -> Unit,
    navigateToUnauthorizedScreen: () -> Unit,
    showSnackbarMutableSharedFlow: MutableSharedFlow<String>,
) {
    val viewModel = koinViewModel<SettingViewModel>()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUserOwner by viewModel.isUserOwner.collectAsState()
    val onSignOutClicked = remember {
        {
            viewModel.onSignOutClicked()
            navigateToUnauthorizedScreen()
        }
    }
    val onStopObserveSharedDatabaseClicked: () -> Unit = remember { { viewModel.onStopObserveSharedDatabaseClicked() } }
    val scope: CoroutineScope = rememberCoroutineScope()
    val userData by viewModel.userData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startObserveSettingData()
        viewModel.onError
            .onEach { showSnackbarMutableSharedFlow.emit(it) }
            .launchIn(scope)
    }

    if (isLoading) {
        showProgress()
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .padding(12.dp)
                        .weight(1f),
                    text = userData?.email ?: "loading... or wtf who are you?!?", // todo something like loadings?
                )
                Button(
                    onClick = onSignOutClicked,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                ) {
                    Text(
                        text = "sign out",
                        color = Color.Black,
                    )
                }
            }
            when (isUserOwner) {
                true -> {
                    SharingSettingsButton(navigateToSharingSettingsScreen = navigateToSharingSettingsScreen)
                }
                false -> {
                    StopObserveSharedDatabaseButton(onStopObserveSharedDatabaseClicked = onStopObserveSharedDatabaseClicked)
                }
                else -> {
                    Timber.d("isUserOwner.value == null ") // todo xz what the logic should be there  something like loadings?
                }
            }
            CategoriesScreenButton(navigateToCategoriesScreen = navigateToCategoriesScreen)
        }
    }
}

@Composable
fun StopObserveSharedDatabaseButton(
    onStopObserveSharedDatabaseClicked: () -> Unit,
) {
    Button(
        onClick = { onStopObserveSharedDatabaseClicked() },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.padding(end = 12.dp),
                imageVector = Icons.Outlined.Close,
                contentDescription = Icons.Outlined.Close.name
            )
            Text(
                text = "stop observe shared database",
                color = Color.Black,
            )
        }
    }
}

@Composable
fun CategoriesScreenButton(navigateToCategoriesScreen: () -> Unit) {
    Button(
        onClick = navigateToCategoriesScreen,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.padding(end = 12.dp),
                imageVector = Icons.Outlined.Category,
                contentDescription = Icons.Outlined.Category.name
            )
            Text(
                text = "categories",
                color = Color.Black,
            )
        }
    }
}

@Composable
fun SharingSettingsButton(
    navigateToSharingSettingsScreen: () -> Unit,
) {
    Button(
        onClick = navigateToSharingSettingsScreen,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.padding(end = 12.dp),
                imageVector = Icons.Outlined.Share,
                contentDescription = Icons.Outlined.Share.toString(),
            )
            Text(
                text = "sharing settings",
                color = Color.Black,
            )
        }
    }
}

@Composable
fun showProgress() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color.Green)
    }
}

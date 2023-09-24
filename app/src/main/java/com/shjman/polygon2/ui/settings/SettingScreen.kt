package com.shjman.polygon2.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingScreen(
    navigateToCategoriesScreen: () -> Unit,
    navigateToSharingSettingsScreen: () -> Unit,
    navigateToUnauthorizedScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<SettingViewModel>()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUserObserveSomebody by viewModel.isUserObserveSomebody.collectAsState()
    val userData by viewModel.userData.collectAsState()
    val onSignOutClick = remember { { viewModel.onSignOutClicked() } }
    val onStopObserveSharedDatabaseClick: () -> Unit = remember { { viewModel.onStopObserveSharedDatabaseClicked() } }

    LaunchedEffect(Unit) {
        viewModel.signOutCompleted
            .onEach { navigateToUnauthorizedScreen() }
            .launchIn(scope)
    }
    Box(
        modifier = modifier,
    )
    {
        if (isLoading) {
            ShowProgress()
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
                        text = ("Hello " + userData?.email)  // todo something like loadings?
                    )
                    Button(
                        onClick = onSignOutClick,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    ) {
                        Text(
                            text = "sign out",
                            color = Color.Black,
                        )
                    }
                }
                when (isUserObserveSomebody) {
                    true -> {
                        Text(
                            text = "you are observing somebody",
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) // todo add email who does user observe
                        StopObserveSharedDatabaseButton(
                            onStopObserveSharedDatabaseClicked = onStopObserveSharedDatabaseClick,
                        )
                    }
                    false -> {
                        SharingSettingsButton(
                            navigateToSharingSettingsScreen = navigateToSharingSettingsScreen,
                        )
                    }
                    else -> Text(text = "is loading...")
                }
                CategoriesScreenButton(navigateToCategoriesScreen = navigateToCategoriesScreen)
            }
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
fun ShowProgress() {
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

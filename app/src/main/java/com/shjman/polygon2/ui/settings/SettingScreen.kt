package com.shjman.polygon2.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SettingScreen(
    isLoading: MutableState<Boolean> = remember { mutableStateOf(true) },
    navigateToCategoriesScreen: () -> Unit,
    navigateToSharingSettingsScreen: () -> Unit,
    navigateToUnauthorizedScreen: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    settingViewModel: SettingViewModel,
    userDataState: MutableState<FirebaseUser?> = remember { mutableStateOf(null) },
    isUserOwner: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val onSignOutClicked = {
        settingViewModel.onSignOutClicked()
        navigateToUnauthorizedScreen()
    }
    LaunchedEffect(Unit) {
        settingViewModel.startObserveSettingData()
        settingViewModel.isLoading
            .onEach { isLoading.value = it }
            .launchIn(scope)
        settingViewModel.userData
            .onEach {
                userDataState.value = it
            }
            .launchIn(scope)
    }

    if (isLoading.value) {
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
                    text = userDataState.value?.email ?: "wtf who are you?!?",
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
            if (isUserOwner.value) {
                SharingSettingsButton(navigateToSharingSettingsScreen = navigateToSharingSettingsScreen) // todo show only for dataBase owner - add check
            }
            CategoriesScreenButton(navigateToCategoriesScreen = navigateToCategoriesScreen)
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
                imageVector = Icons.Default.Category,
                contentDescription = Icons.Default.Category.name
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
                imageVector = Icons.Default.Share,
                contentDescription = Icons.Default.Share.toString(),
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

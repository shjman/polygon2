package com.shjman.polygon2.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.shjman.polygon2.ui.Screens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SettingScreen(
    settingViewModel: SettingViewModel,
    isLoadingUIState: MutableState<Boolean> = remember { mutableStateOf(true) },
    userDataState: MutableState<FirebaseUser?> = remember { mutableStateOf(null) },
    scope: CoroutineScope = rememberCoroutineScope(),
    onSharingSpendingsClicked: () -> Unit,
    navHostController: NavHostController,
) {
    LaunchedEffect(Unit) {
        settingViewModel.startObserveSettingData()
        settingViewModel.isLoading
            .onEach { isLoadingUIState.value = it }
            .launchIn(scope)

        settingViewModel.userData
            .onEach {
                userDataState.value = it
            }
            .launchIn(scope)

    }

    if (isLoadingUIState.value) {
        showProgress()
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = userDataState.value?.email ?: "wtf is your email??",
            )
            Button(
                // todo show only for dataBase owner - add check
                onClick = { navHostController.navigate(Screens.SharingSettings.screenRoute) },
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

package com.shjman.polygon2.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shjman.polygon2.data.TrustedUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SharingSettingsScreen(
    sharingSettingViewModel: SharingSettingViewModel,
    scope: CoroutineScope = rememberCoroutineScope(),
    trustedUsersState: MutableState<List<TrustedUser>?> = remember { mutableStateOf(null) },
    navigateToAddTrustedUser: () -> Unit,
    onAddTrustedUserClicked: () -> Unit = { sharingSettingViewModel.addTrustedUserClicked() },
) {
    LaunchedEffect(Unit) {
        sharingSettingViewModel.startObserveTrustedEmails()
        sharingSettingViewModel.trustedUsers
            .onEach { trustedUsersState.value = it }
            .launchIn(scope)
        sharingSettingViewModel.onAddTrustedUserClicked
            .onEach { navigateToAddTrustedUser() }
            .launchIn(scope)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Text(
            text = "List of trusted users:"
        )
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            val trustedUsers = trustedUsersState.value
            when {
                trustedUsers == null -> {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "is loading...", // todo disable button until loading?
                        )
                    }
                }
                trustedUsers.isEmpty() -> {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "List of trusted users is empty",
                        )
                    }
                }
                else -> {
                    trustedUsers.onEach {
                        item(key = it.email) {
                            TrustedUserView(
                                trustedUser = it,
                            )
                        }
                    }
                }
            }
        }
        AddTrustedUserButton(
            onAddTrustedUserClicked = onAddTrustedUserClicked,
        )
    }
}

@Composable
fun AddTrustedUserButton(
    onAddTrustedUserClicked: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onAddTrustedUserClicked,
    ) {
        Text(
            text = "add trusted user",
        )
    }
}

@Composable
fun TrustedUserView(
    trustedUser: TrustedUser,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 4.dp,
    ) {
        Row()
        {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                text = trustedUser.email,
            )
        }
    }
}

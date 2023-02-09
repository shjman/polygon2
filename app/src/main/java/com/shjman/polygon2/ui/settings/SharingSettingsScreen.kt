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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharingSettingsScreen(
    navigateToAddTrustedUser: () -> Unit,
    sendInviteLink: (String) -> Unit,
    showSnackbarMutableSharedFlow: MutableSharedFlow<String>,
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val viewModel = koinViewModel<SharingSettingViewModel>()
    val trustedUsers by viewModel.trustedUsers.collectAsState()
    val onSendInviteLinkButtonClicked: () -> Unit = remember { { viewModel.onSendInviteLinkButtonClicked() } }
    val onAddTrustedUserClicked: () -> Unit = remember { { viewModel.addTrustedUserClicked() } }

    LaunchedEffect(Unit) {
        viewModel.startObserveTrustedEmails()
        viewModel.onError
            .onEach { showSnackbarMutableSharedFlow.emit(it) }
            .launchIn(scope)
        viewModel.onAddTrustedUserClicked
            .onEach { navigateToAddTrustedUser() }
            .launchIn(scope)
        viewModel.onSendInviteLinkButtonClicked
            .onEach { sendInviteLink(it) }
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
            val trustedUsersList = trustedUsers
            when {
                trustedUsersList == null -> {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "is loading...", // todo disable button until loading?
                        )
                    }
                }
                trustedUsersList.isEmpty() -> {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "List of trusted users is empty",
                        )
                    }
                }
                else -> {
                    trustedUsersList.onEach {
                        item(key = it.email) {
                            TrustedUserView(
                                trustedUser = it,
                            )
                        }
                    }
                }
            }
        }
        SendInviteLinkButton(
            onSendInviteLinkButtonClicked = onSendInviteLinkButtonClicked,
        )
        AddTrustedUserButton(
            onAddTrustedUserClicked = onAddTrustedUserClicked,
        )
    }
}

@Composable
fun SendInviteLinkButton(
    onSendInviteLinkButtonClicked: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onSendInviteLinkButtonClicked,
    ) {
        Text(
            text = "send invite link",
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

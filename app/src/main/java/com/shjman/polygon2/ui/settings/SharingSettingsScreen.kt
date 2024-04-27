package com.shjman.polygon2.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shjman.polygon2.data.TrustedUser
import com.shjman.polygon2.ui.KEY_SHARED_DOCUMENT_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharingSettingsScreen(
    navigateToAddTrustedUser: () -> Unit,
) {
    val context = LocalContext.current
    val sendInviteLink: (String) -> Unit = remember {
        { documentPath ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://shjman/spendings?$KEY_SHARED_DOCUMENT_PATH=$documentPath")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "send invite link of your database"))
        }
    }

    val scope: CoroutineScope = rememberCoroutineScope()
    val viewModel = koinViewModel<SharingSettingViewModel>()
    val trustedUsers by viewModel.trustedUsers.collectAsState()
    val onSendInviteLinkButtonClicked: () -> Unit = remember { { viewModel.onSendInviteLinkButtonClicked() } }
    val onAddTrustedUserClicked: () -> Unit = remember { { viewModel.addTrustedUserClicked() } }

    LaunchedEffect(Unit) {
        viewModel.startObserveTrustedEmails()
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
        elevation = CardDefaults.cardElevation(4.dp),
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

package com.shjman.polygon2.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTrustedUserScreen(
    popBackStack: () -> Unit,
) {
    val viewModel = koinViewModel<AddTrustedUserViewModel>()
    val isProceedButtonEnabled by viewModel.isProceedButtonEnabled.collectAsState()
    val scope: CoroutineScope = rememberCoroutineScope()
    val trustedUserEmail by viewModel.trustedUserEmail.collectAsState()

    BackHandler { viewModel.onBackClicked() }

    LaunchedEffect(Unit) {
        viewModel.popBackStack
            .onEach { popBackStack() }
            .launchIn(scope)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text(text = "write a trusted user email") },
            value = trustedUserEmail,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isProceedButtonEnabled) {
                        viewModel.onDoneClicked()
                    }
                },
            ),
            shape = RoundedCornerShape(12.dp),
            onValueChange = { viewModel.trustedUserEmailChanged(it) },
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { viewModel.onDoneClicked() },
            enabled = isProceedButtonEnabled,
        ) {
            Text(
                text = "done",
            )
        }
    }
}
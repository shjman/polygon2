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

@Composable
fun AddTrustedUserScreen(
    addTrustedUserViewModel: AddTrustedUserViewModel,
    trustedUserEmail: MutableState<String> = remember { mutableStateOf("") },
    isProceedButtonEnabled: MutableState<Boolean> = remember { mutableStateOf(false) },
    scope: CoroutineScope = rememberCoroutineScope(),
    popBackStack: () -> Unit,
) {
    BackHandler { addTrustedUserViewModel.onBackClicked() }

    LaunchedEffect(Unit) {
        addTrustedUserViewModel.trustedUserEmail
            .onEach { trustedUserEmail.value = it }
            .launchIn(scope)
        addTrustedUserViewModel.isProceedButtonEnabled
            .onEach { isProceedButtonEnabled.value = it }
            .launchIn(scope)
        addTrustedUserViewModel.popBackStack
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
            value = trustedUserEmail.value,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isProceedButtonEnabled.value) {
                        addTrustedUserViewModel.onDoneClicked()
                    }
                },
            ),
            shape = RoundedCornerShape(12.dp),
            onValueChange = { addTrustedUserViewModel.trustedUserEmailChanged(it) },
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { addTrustedUserViewModel.onDoneClicked() },
            enabled = isProceedButtonEnabled.value,
        ) {
            Text(
                text = "done",
            )
        }
    }
}
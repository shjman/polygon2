package com.shjman.polygon2.ui.categories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun EditCategoryScreen(
    editCategoryViewModel: EditCategoryViewModel,
    showSnackbarMutableSharedFlow: MutableSharedFlow<String>,
    popBackStack: () -> Unit,
) {
    val categoryName by editCategoryViewModel.categoryName.collectAsState()
    val isProceedButtonEnabled by editCategoryViewModel.isProceedButtonEnabled.collectAsState()
    val scope: CoroutineScope = rememberCoroutineScope()

    BackHandler { editCategoryViewModel.onBackClicked() }

    LaunchedEffect(Unit) {
        editCategoryViewModel.onError
            .onEach { showSnackbarMutableSharedFlow.emit(it) }
            .launchIn(scope)
        editCategoryViewModel.popBackStack
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
            label = { Text(text = "write category") },
            value = categoryName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isProceedButtonEnabled) {
                        editCategoryViewModel.onDoneClicked()
                    }
                },
            ),
            shape = RoundedCornerShape(12.dp),
            onValueChange = { editCategoryViewModel.categoryValueChanged(it) },
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { editCategoryViewModel.onDoneClicked() },
            enabled = isProceedButtonEnabled,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
        ) {
            Text(
                text = "done",
                color = Color.White,
            )
        }
    }
}
package com.shjman.polygon2.ui.categories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun EditCategoryScreen(
    editCategoryViewModel: EditCategoryViewModel,
    category: MutableState<String> = remember { mutableStateOf("") },
    isProceedButtonEnabled: MutableState<Boolean> = remember { mutableStateOf(false) },
    scope: CoroutineScope = rememberCoroutineScope(),
    popBackStack: () -> Unit,
) {
    BackHandler { editCategoryViewModel.onBackClicked() }

    LaunchedEffect(Unit) {
        editCategoryViewModel.categoryName
            .onEach { category.value = it }
            .launchIn(scope)
        editCategoryViewModel.isProceedButtonEnabled
            .onEach { isProceedButtonEnabled.value = it }
            .launchIn(scope)
        editCategoryViewModel.popBackStack
            .onEach { popBackStack() }
            .launchIn(scope)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(1f),
            value = category.value,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { editCategoryViewModel.onDoneClicked() },
            ),
            onValueChange = { editCategoryViewModel.categoryValueChanged(it) },
            placeholder = { Text("write category") },
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { editCategoryViewModel.onDoneClicked() },
            enabled = isProceedButtonEnabled.value,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
        ) {
            Text(
                text = "done",
                color = Color.White,
            )
        }
    }
}
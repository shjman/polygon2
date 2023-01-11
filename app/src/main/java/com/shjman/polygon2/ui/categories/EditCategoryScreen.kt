package com.shjman.polygon2.ui.categories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text(text = "write category") },
            value = category.value,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { editCategoryViewModel.onDoneClicked() },
            ),
            shape = RoundedCornerShape(12.dp),
            onValueChange = { editCategoryViewModel.categoryValueChanged(it) },
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
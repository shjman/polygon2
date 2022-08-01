package com.shjman.polygon2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Face
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun SpentScreen(
    spentViewModel: SpentViewModel,
    categories: MutableState<List<Category>> = remember { mutableStateOf(emptyList()) },
    selectedCategory: Category = spentViewModel.selectedCategory.observeAsState(Category.empty()).value,
    isLoadingUI: MutableState<Boolean> = remember { mutableStateOf(false) },
    amountSpent: Int = spentViewModel.amountSpent.observeAsState(0).value,
    note: String = spentViewModel.note.observeAsState("").value,
    isDropdownMenuExpanded: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    LaunchedEffect(Unit) {
        categories.value = spentViewModel.getAllCategories()
        spentViewModel.isLoading.collect { isLoadingUI.value = it }
        Timber.e("aaaa after spentViewModel.isLoading.collect { isLoadingUI.value = it }")
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(text = "home spent screen", color = Color.Green)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InputCategoryView(
            categories = categories,
            selectedCategory = selectedCategory,
            isDropdownMenuExpanded = isDropdownMenuExpanded,
            onDropdownMenuItemClicked = {
                spentViewModel.onSelectedCategoryChanged(it)
                isDropdownMenuExpanded.value = false
            }
        )
        InputAmountSpendView(
            isLoadingUI = isLoadingUI,
            amountSpent = amountSpent,
            onSpentValueChanged = { spentViewModel.onAmountSpentChanged(it.toIntOrNull() ?: 0) },
        )
        InputNoteView(
            isLoadingUI = isLoadingUI,
            note = note,
            onNoteChanged = { spentViewModel.onNoteChanged(it) },
        )
        SaveButton(isLoadingUI) { spentViewModel.onSaveButtonClicked() }
    }
}

@Composable
fun InputCategoryView(
    categories: State<List<Category>>,
    selectedCategory: Category,
    isDropdownMenuExpanded: MutableState<Boolean>,
    onDropdownMenuItemClicked: (Category) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable { isDropdownMenuExpanded.value = !isDropdownMenuExpanded.value }
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "category:",
            modifier = Modifier.padding(4.dp),
        )
        Text(
            text = selectedCategory.name,
            modifier = Modifier.padding(4.dp),
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = Icons.Filled.ArrowDropDown.toString(),
        )
        DropdownMenu(
            expanded = isDropdownMenuExpanded.value,
            onDismissRequest = { isDropdownMenuExpanded.value = false },
        ) {
            categories.value.forEach { category ->
                DropdownMenuItem(
                    onClick = { onDropdownMenuItemClicked(category) }
                ) {
                    val isSelected = category == selectedCategory
                    val style = if (isSelected) {
                        MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.secondary
                        )
                    } else {
                        MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    Text(text = category.name, style = style)
                }
            }
        }
    }
}

@Composable
fun InputAmountSpendView(
    isLoadingUI: MutableState<Boolean>,
    amountSpent: Int,
    onSpentValueChanged: (String) -> Unit,
) {
    TextField(
        value = if (amountSpent == 0) "" else amountSpent.toString(),
        singleLine = true,
        enabled = !isLoadingUI.value,
        onValueChange = onSpentValueChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = { Icon(Icons.Outlined.Face, contentDescription = "trailing icon") },
        placeholder = { Text("enter the amount spent") }
    )
}

@Composable
fun InputNoteView(
    isLoadingUI: MutableState<Boolean>,
    note: String,
    onNoteChanged: (String) -> Unit,
) {
    TextField(
        value = note,
        onValueChange = onNoteChanged,
        maxLines = 4,
        enabled = !isLoadingUI.value,
        placeholder = { Text("write note") },
    )
}

@Composable
fun SaveButton(isLoading: MutableState<Boolean>, onSaveAmountClicked: () -> Unit) {
    Button(
        onClick = onSaveAmountClicked,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(color = Color.Green)
        } else {
            Text(text = "save this amount", color = Color.White)
        }
    }
}

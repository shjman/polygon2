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
import androidx.lifecycle.LifecycleCoroutineScope

@Composable
fun SpentScreen(
    lifecycleScope: LifecycleCoroutineScope,
    spentViewModel: SpentViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "home spent screen", color = Color.Green)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputCategoryView()
        InputAmountSpendScreen(lifecycleScope, spentViewModel)
    }
}

@Composable
fun InputCategoryView() {
    val categories = listOf("home", "car", "pets", "eat", "entertainment")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable { expanded = !expanded }
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "category:",
            modifier = Modifier.padding(4.dp),
        )
        Text(
            text = selectedCategory,
            modifier = Modifier.padding(4.dp),
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = Icons.Filled.ArrowDropDown.toString(),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            categories.forEach { category ->
                DropdownMenuItem(onClick = {
                    selectedCategory = category
                    expanded = false
                }) {
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
                    Text(text = category, style = style)
                }
            }
        }
    }
}

@Composable
fun InputAmountSpendScreen(lifecycleScope: LifecycleCoroutineScope, spentViewModel: SpentViewModel) {
    val amountSpent: Int by spentViewModel.amountSpent.observeAsState(0)
    val isLoadingUI = remember { mutableStateOf(false) }
//    val isLoadingViewModel : Boolean by spentViewModel.isLoading.observeAsState(false)
    lifecycleScope.launchWhenCreated {
        spentViewModel.isLoading.collect {
            isLoadingUI.value = it
        }
    }
    TextField(
        isLoadingUI = isLoadingUI,
        amountSpent = amountSpent
    ) { spentViewModel.onAmountSpentChanged(it.toIntOrNull() ?: 0) }
    SaveButton(isLoadingUI) { spentViewModel.onSaveButtonClicked() }
}

@Composable
fun TextField(isLoadingUI: MutableState<Boolean>, amountSpent: Int, amountSpentChanged: (String) -> Unit) {
    TextField(
        value = if (amountSpent == 0) "" else amountSpent.toString(),
        singleLine = true,
        enabled = !isLoadingUI.value,
        onValueChange = amountSpentChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = { Icon(Icons.Outlined.Face, contentDescription = "trailing icon") },
        placeholder = { Text("enter the amount spent") }
    )
}

@Composable
fun SaveButton(isLoading: MutableState<Boolean>, onSaveAmountClicked: () -> Unit) {
    Button(
        onClick = onSaveAmountClicked,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(color = Color.Green)
        } else {
            Text(text = "save this amount", color = Color.White)
        }
    }
}

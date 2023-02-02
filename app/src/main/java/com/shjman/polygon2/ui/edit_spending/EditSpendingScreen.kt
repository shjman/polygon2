package com.shjman.polygon2.ui.edit_spending

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.Spending
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EditSpendingScreen(
    localDateTimeSpending: LocalDateTime,
    editSpendingViewModel: EditSpendingViewModel,
    isLoading: MutableState<Boolean> = remember { mutableStateOf(true) },
    spendingState: MutableState<Spending?> = remember { mutableStateOf(null) },
    categories: MutableState<List<Category>?> = remember { mutableStateOf(null) },
    isDropdownMenuExpanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context,
    scaffoldState: ScaffoldState,
    navigatePopBackClicked: () -> Unit,
    focusManager: FocusManager = LocalFocusManager.current,
) {
    LaunchedEffect(Unit) {
        editSpendingViewModel.loadSpending(localDateTimeSpending)
        editSpendingViewModel.isLoading
            .onEach { isLoading.value = it }
            .launchIn(scope)
        editSpendingViewModel.spending
            .onEach { spendingState.value = it }
            .launchIn(scope)
        editSpendingViewModel.categories
            .onEach { categories.value = it }
            .launchIn(scope)
        editSpendingViewModel.showSpendingUpdated
            .onEach {
                scope.launch {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Spending updated. You will be moved back",
                        actionLabel = "OK.Go"
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> navigatePopBackClicked()
                        SnackbarResult.ActionPerformed -> navigatePopBackClicked()
                    }
                }
            }
            .launchIn(scope)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val spending = spendingState.value
        when {
            isLoading.value -> CircularProgressIndicator(color = Color.Green)
            spending == null -> Text(text = "error loading data")
            else -> {
                DateRowView(
                    context = context,
                    date = spending.date,
                    editSpendingViewModel = editSpendingViewModel,
                )
                AmountRowView(
                    amountSpent = spending.spentAmount,
                    editSpendingViewModel = editSpendingViewModel,
                )
                NoteRowView(
                    note = spending.note,
                    editSpendingViewModel = editSpendingViewModel,
                )
                InputCategoryView(
                    categories = categories.value,
                    selectedCategory = spending.category,
                    isDropdownMenuExpanded = isDropdownMenuExpanded,
                    onDropdownMenuItemClicked = {
                        editSpendingViewModel.onSelectedCategoryChanged(it)
                        isDropdownMenuExpanded.value = false
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navigatePopBackClicked() },
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .weight(0.5f)
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White,
                        )
                    }
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            editSpendingViewModel.onSaveButtonClicked()
                        },
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .weight(0.5f)
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun NoteRowView(
    note: String?,
    editSpendingViewModel: EditSpendingViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "note == ",
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .weight(0.3f),
        )
        TextField(
            modifier = Modifier.weight(0.7f),
            value = note ?: "",
            onValueChange = { editSpendingViewModel.onNoteChanged(it) },
            placeholder = { Text("enter note") },
        )
    }
}

@Composable
fun AmountRowView(
    amountSpent: Int?,
    editSpendingViewModel: EditSpendingViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "amount == ",
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .weight(0.3f),
        )
        TextField(
            modifier = Modifier.weight(0.7f),
            value = amountSpent?.toString() ?: "",
            onValueChange = { editSpendingViewModel.onSpentAmountChanged(calculateLimitAmount(it) ?: 0) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("enter the amount spent") }
        )
    }
}

@Composable
fun DateRowView(
    context: Context,
    date: LocalDateTime?,
    editSpendingViewModel: EditSpendingViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "date == ",
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .weight(0.3f),
        )
        Button(
            modifier = Modifier.weight(0.7f),
            onClick = {
                getDatePickerDialog(context, editSpendingViewModel, date)?.show()
                getTimePickerDialog(context, editSpendingViewModel, date)?.show()
            },
        ) {
            Text(text = date?.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER)) ?: "")
        }
    }
}


@Composable
fun InputCategoryView(
    categories: List<Category>?,
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
        when {
            categories.isNullOrEmpty() -> {
                Text(
                    text = "error loading data",
                    modifier = Modifier.padding(4.dp),
                )
            }
            else -> {
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
                    categories.forEach { category ->
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
    }
}

fun getDatePickerDialog(
    context: Context,
    editSpendingViewModel: EditSpendingViewModel,
    localDateTime: LocalDateTime?,
): DatePickerDialog? {
    return localDateTime?.let {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                editSpendingViewModel.onSpendingDateChanged(year, month + 1, dayOfMonth)
            },
            it.year,
            it.monthValue - 1,
            it.dayOfMonth,
        )
    }
}

fun getTimePickerDialog(
    context: Context,
    editSpendingViewModel: EditSpendingViewModel,
    localDateTime: LocalDateTime?,
): TimePickerDialog? {
    return localDateTime?.let {
        TimePickerDialog(
            context,
            { _, newHour: Int, newMinute: Int ->
                editSpendingViewModel.onSpendingTimeChanged(newHour, newMinute)
            },
            it.hour,
            it.minute,
            true,
        )
    }
}

fun calculateLimitAmount(text: String): Int? {
    val maxIntAmount = 99999999 // todo fix it
    val amountLengthLimit = 9
    return if (text.length >= amountLengthLimit) maxIntAmount else text.toIntOrNull()
}

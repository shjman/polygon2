package com.shjman.polygon2.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.Spending
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EditSpendingScreen(
    localDateTime: LocalDateTime,
    editSpendingViewModel: EditSpendingViewModel,
    spending: MutableState<Spending?> = remember { mutableStateOf(null) },
    amountSpent: MutableState<Int?> = remember { mutableStateOf(null) },
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context,
    scaffoldState: ScaffoldState,
    navigatePopBackClicked: () -> Unit,
    focusManager: FocusManager = LocalFocusManager.current,
) {
    LaunchedEffect(Unit) {
        editSpendingViewModel.loadSpending(localDateTime)
        editSpendingViewModel.spending
            .onEach { spending.value = it }
            .launchIn(scope)
        editSpendingViewModel.amountSpent
            .onEach { amountSpent.value = it }
            .launchIn(scope)
        editSpendingViewModel.showSpendingUpdated
            .onEach {
                scope.launch {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Spending updated",
                        actionLabel = "OK. go back"
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> Timber.e("aaaa Snackbar Dismissed")
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
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val spendingValue = spending.value
        if (spendingValue != null) {
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
                        getDatePickerDialog(context, editSpendingViewModel, spending)?.show()
                        getTimePickerDialog(context, editSpendingViewModel, spending)?.show()
                    },
                ) {
                    Text(text = spendingValue.date.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER)))
                }
            }
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
                    value = if (amountSpent.value == null) "" else amountSpent.value.toString(),
                    onValueChange = { editSpendingViewModel.onAmountSpentChanged(calculateLimitAmount(it)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("enter the amount spent") }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "note == ")
                Text(text = spendingValue.note)
            }
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
                    Text(text = "Cancel")
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
        } else {
            CircularProgressIndicator(color = Color.Green)
        }
    }
}

fun getDatePickerDialog(
    context: Context,
    editSpendingViewModel: EditSpendingViewModel,
    spending: MutableState<Spending?>,
): DatePickerDialog? {
    return spending.value?.date?.let {
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
    spending: MutableState<Spending?>,
): TimePickerDialog? {
    return spending.value?.date?.let {
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
    val maxIntAmount = 99999999
    val amountLengthLimit = 9
    return if (text.length >= amountLengthLimit) maxIntAmount else text.toIntOrNull()
}

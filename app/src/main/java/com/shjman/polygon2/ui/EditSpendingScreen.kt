package com.shjman.polygon2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.Spending
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EditSpendingScreen(
    localDateTime: LocalDateTime,
    editSpendingViewModel: EditSpendingViewModel,
    spending: MutableState<Spending?> = remember { mutableStateOf(null) },
    amountSpent: MutableState<Int?> = remember { mutableStateOf(null) },
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    LaunchedEffect(Unit) {
        editSpendingViewModel.loadSpending(localDateTime)
        editSpendingViewModel.spending
            .onEach { spending.value = it }
            .launchIn(scope)
        editSpendingViewModel.amountSpent
            .onEach { amountSpent.value = it }
            .launchIn(scope)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val spendingValue = spending.value
        if (spendingValue != null) {
            Row {
                Text(text = "date == ")
                Text(text = spendingValue.date.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER)))
            }
            Row {
                Text(text = "amount == ")

                TextField(
                    value = if (amountSpent.value == null) "" else amountSpent.value.toString(),
                    onValueChange = { editSpendingViewModel.onAmountSpentChanged(calculateLimitAmount(it)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("enter the amount spent") }
                )
            }
            Row {
                Text(text = "note == ")
                Text(text = spendingValue.note.toString())
            }
        } else {
            CircularProgressIndicator(color = Color.Green)
        }
    }
}

fun calculateLimitAmount(text: String): Int? {
    val maxIntAmount = 99999999
    val amountLengthLimit = 9
    return if (text.length >= amountLengthLimit) maxIntAmount else text.toIntOrNull()
}

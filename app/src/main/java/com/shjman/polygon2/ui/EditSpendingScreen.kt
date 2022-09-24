package com.shjman.polygon2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    LaunchedEffect(Unit) {
        editSpendingViewModel.loadSpending(localDateTime)
        editSpendingViewModel.spending
            .onEach { spending.value = it }
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
                Text(text = spendingValue.spentAmount.toString())
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
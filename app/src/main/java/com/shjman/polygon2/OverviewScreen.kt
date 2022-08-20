package com.shjman.polygon2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun OverviewScreen(
    lifecycleScope: LifecycleCoroutineScope,
    spentViewModel: SpentViewModel,
) {
    val onSpendingClicked = { spending: Spending -> Timber.d("clicked on == $spending") }
    val allSpending: MutableState<List<Spending>?> = remember { mutableStateOf(null) }
    lifecycleScope.launchWhenCreated {
        allSpending.value = spentViewModel.getAllSpending()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Overview screen",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        val allSpendingValue = allSpending.value
        if (allSpendingValue == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.Green)
            }
        } else if (allSpendingValue.isEmpty()) {
            Text(text = "no data / empty")
        } else {
            LazyColumn {
//                itemsIndexed(allSpendingValue) { index, item ->
//                    SpendingItem(spending = item, onSpendingClicked = onSpendingClicked)
//                }
                item {
                    val currentLocalDateTime = LocalDateTime.now()
                    val beginOfCurrentMonth = LocalDateTime.of(
                        currentLocalDateTime.year,
                        currentLocalDateTime.month,
                        1,
                        0,
                        0
                    )
//                    val beginOfNextMonth = beginOfCurrentMonth.plusMonths(1)
//                    val lastDayOfCurrentMonth = LocalDateTime.of(
//                        currentLocalDateTime.year,
//                        currentLocalDateTime.month,
//                        currentLocalDateTime.month.maxLength(),
//                        23,
//                        59
//                    )
                    var amountByMonth = 0
                    allSpendingValue
                        .filter { it.date?.isAfter(beginOfCurrentMonth) == true }
                        .onEach { spending -> spending.spentAmount?.let { amountByMonth += it } }
                    Text(text = "amount spent this month == $amountByMonth")
                }

                allSpendingValue.onEach {
                    item(
                        key = it.date.toString(),
                    ) {
                        SpendingItem(spending = it, onSpendingClicked = onSpendingClicked)
                    }
                }
//                items(
//                    items = allSpendingValue,
//                    itemContent = {
//                        SpendingItem(spending = it, onSpendingClicked = onSpendingClicked)
//                    }
//                )
            }
        }
    }
}

@Composable
fun SpendingItem(spending: Spending, onSpendingClicked: (Spending) -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onSpendingClicked(spending) },
        elevation = 4.dp,
    ) {
        Row {
            val dateStr = spending.date?.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
            Text(
                "date = $dateStr" +
                        "\nspentAmount = ${spending.spentAmount}" +
                        "\ncategory = ${spending.category}" +
                        "\nnote = ${spending.note}"
            )
        }
    }
}
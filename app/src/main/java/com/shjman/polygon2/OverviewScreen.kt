package com.shjman.polygon2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun OverviewScreen(
    spentViewModel: SpentViewModel,
    allSpending: MutableState<List<Spending>?> = remember { mutableStateOf(null) },
) {
    LaunchedEffect(Unit) {
        allSpending.value = spentViewModel.getAllSpending()
    }
    val onSpendingClicked = { spending: Spending -> Timber.d("clicked on == $spending") }
    var isTopAppBarDropdownMenuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Overview screen") },
                actions = {
                    IconButton(onClick = { isTopAppBarDropdownMenuExpanded = !isTopAppBarDropdownMenuExpanded }) {
                        Icon(Icons.Default.MoreVert, "")
                    }
                    DropdownMenu(
                        expanded = isTopAppBarDropdownMenuExpanded,
                        onDismissRequest = { isTopAppBarDropdownMenuExpanded = false }
                    ) {
                        DropdownMenuItem(onClick = { /*TODO*/ }) {
                            Text("Compare monthly spends")
                        }
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
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
                            var amountByLastMonth = 0
                            val allSpendingValueFilteredByLastMonth = allSpendingValue.filter { it.date?.isAfter(beginOfCurrentMonth) == true }
                            allSpendingValueFilteredByLastMonth.onEach { spending -> spending.spentAmount?.let { amountByLastMonth += it } }
                            Text(text = "amount spent this month == $amountByLastMonth")
                            val categories = mutableSetOf<String>()
                            allSpendingValueFilteredByLastMonth.onEach { spending -> spending.category?.let { it -> categories.add(it) } }
                            when {
                                categories.isEmpty() -> Text(text = "categories == empty")
                                else -> {
                                    val amountsByCategories = StringBuffer()
                                    categories.onEach { categoryString ->
                                        var amountByCategory = 0
                                        allSpendingValueFilteredByLastMonth
                                            .filter { it.category == categoryString }
                                            .onEach { spending -> spending.spentAmount?.let { amountByCategory += it } }
                                        amountsByCategories.append("$categoryString == $amountByCategory, ")
                                    }
                                    Text(text = "$amountsByCategories")
                                }
                            }
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
        })
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
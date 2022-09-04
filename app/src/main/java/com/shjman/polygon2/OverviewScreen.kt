package com.shjman.polygon2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
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
    var overviewType by remember { mutableStateOf(OverviewType.STANDARD) } // isMonthlyComparison: Boolean -> can be simplified
    Scaffold(
        topBar = {
            topBar(
                overviewType = overviewType,
                onMonthlyComparisonTypeChanged = { overviewType = overviewType.switch() }
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
                        val beginOfCurrentMonth = LocalDateTime.now().beginOfCurrentMonth()
                        val allSpendingValueFilteredByLastMonth = allSpendingValue.filter { it.date?.isAfter(beginOfCurrentMonth) == true }
                        item { summaryOfMonth(beginOfCurrentMonth, allSpendingValueFilteredByLastMonth) }
                        if (overviewType == OverviewType.STANDARD) {
                            allSpendingValue.onEach { item(key = it.date.toString()) { SpendingItem(spending = it, onSpendingClicked = onSpendingClicked) } }
                        } else {
                            var beginOfPreviousMonth = beginOfCurrentMonth.minusMonths(1)
                            var allSpendingMinusPreviousMonth: List<Spending> = allSpendingValue.minus(allSpendingValueFilteredByLastMonth.toSet())
                            while (allSpendingMinusPreviousMonth.isNotEmpty()) {
                                val beginOfMonth = beginOfPreviousMonth
                                val allSpendingFilteredByLastMonth = allSpendingMinusPreviousMonth.filter { it.date?.isAfter(beginOfPreviousMonth) == true }
                                item { summaryOfMonth(beginOfMonth, allSpendingFilteredByLastMonth) }
                                beginOfPreviousMonth = beginOfPreviousMonth.minusMonths(1)
                                allSpendingMinusPreviousMonth = allSpendingMinusPreviousMonth.minus(allSpendingFilteredByLastMonth.toSet())
                            }
                        }
                    }
                }
            }
        })
}

@Composable
fun topBar(
    overviewType: OverviewType,
    onMonthlyComparisonTypeChanged: (Boolean) -> Unit,
) {
    var isTopAppBarDropdownMenuExpanded by remember { mutableStateOf(false) }
    Timber.e("aaaa topBar() isTopAppBarDropdownMenuExpanded.hashCode() == ${isTopAppBarDropdownMenuExpanded.hashCode()}")
    TopAppBar(
        title = { Text(text = "Overview screen") },
        actions = {
            IconButton(onClick = { isTopAppBarDropdownMenuExpanded = !isTopAppBarDropdownMenuExpanded }) {
                Icon(Icons.Default.MoreVert, "")
            }
            DropdownMenu(
                expanded = isTopAppBarDropdownMenuExpanded,
                onDismissRequest = { isTopAppBarDropdownMenuExpanded = false },
                Modifier.background(colorResource(R.color.lightGray))
            ) {
                DropdownMenuItem(enabled = false, onClick = {}) {
                    Text("Monthly comparison", color = Color.Black)
                    Switch(
                        checked = overviewType == OverviewType.MONTHLY_COMPARISON,
                        onCheckedChange = onMonthlyComparisonTypeChanged
                    )
                }
            }
        }
    )
}

@Composable
fun summaryOfMonth(beginOfCurrentMonth: LocalDateTime, allSpendingFilteredByMonth: List<Spending>) {
    val date = beginOfCurrentMonth.month.toString() + "." + beginOfCurrentMonth.year.toString()
    var amountByMonth = 0
    allSpendingFilteredByMonth.onEach { spending -> spending.spentAmount?.let { amountByMonth += it } }
    Text(text = "amount spent $date == $amountByMonth")
    val categories = mutableSetOf<String>()
    allSpendingFilteredByMonth.onEach { spending -> spending.category?.let { it -> categories.add(it) } }
    when {
        categories.isEmpty() -> Text(text = "categories == empty")
        else -> {
            val amountsByCategories = StringBuffer()
            categories.onEach { categoryString ->
                var amountByCategory = 0
                allSpendingFilteredByMonth
                    .filter { it.category == categoryString }
                    .onEach { spending -> spending.spentAmount?.let { amountByCategory += it } }
                if (categoryString.isBlank()) {
                    amountsByCategories.append("empty category")
                }
                amountsByCategories.append("$categoryString == $amountByCategory, ")
            }
            Text(text = "$amountsByCategories")
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

private fun LocalDateTime.beginOfCurrentMonth(): LocalDateTime = LocalDateTime.of(this.year, this.month, 1, 0, 0)

enum class OverviewType {
    STANDARD,
    MONTHLY_COMPARISON,
}

private fun OverviewType.switch() = when (this) {
    OverviewType.STANDARD -> OverviewType.MONTHLY_COMPARISON
    OverviewType.MONTHLY_COMPARISON -> OverviewType.STANDARD
}
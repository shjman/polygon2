package com.shjman.polygon2.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shjman.polygon2.R
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.LOCALE_DATE_TIME_FORMATTER
import com.shjman.polygon2.data.Spending
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    onEditSpendingClicked: (LocalDateTime) -> Unit,
) {
    val viewModel: OverviewViewModel = koinViewModel()
    val allSpending = viewModel.spendingsFlow.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading
    LaunchedEffect(Unit) {
        viewModel.startObserveSpendings()
    }
    val onSpendingClicked = remember { { spending: Spending -> Timber.d("clicked on == $spending") } }
    val onSpendingLongClicked = remember {
        { spending: Spending, isDropdownMenuExpanded: MutableState<Boolean> ->
            Timber.d("clicked long on == $spending")
            isDropdownMenuExpanded.value = !isDropdownMenuExpanded.value
        }
    }
    var overviewType by remember { mutableStateOf(OverviewType.STANDARD) } // isMonthlyComparison: Boolean -> can be simplified
    val onMonthlyComparisonTypeChanged = remember { { _: Boolean -> overviewType = overviewType.switch() } }
    Scaffold(
        topBar = {
            TopBar(
                overviewType = overviewType,
                onMonthlyComparisonTypeChanged = onMonthlyComparisonTypeChanged
            )
        },
        content = {// todo update without scaffold
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.Green)
                }
            } else {
                val allSpendingValue = allSpending.value
                when {
                    allSpendingValue == null -> Text(text = "is loading...")
                    allSpendingValue.isEmpty() -> Text(text = "no data / empty")
                    else -> {
                        LazyColumn {
                            val beginOfCurrentMonth = LocalDateTime.now().beginOfCurrentMonth()
                            val allSpendingValueFilteredByLastMonth = allSpendingValue.filter { it.date.isAfter(beginOfCurrentMonth) }
                            item { SummaryOfMonth(beginOfCurrentMonth, allSpendingValueFilteredByLastMonth) }
                            if (overviewType == OverviewType.STANDARD) {
                                allSpendingValue.onEach {
                                    item(key = it.uuid) {
                                        SpendingItem(
                                            spending = it,
                                            onSpendingClicked = onSpendingClicked,
                                            onSpendingLongClicked = onSpendingLongClicked,
                                            onEditSpendingClicked = onEditSpendingClicked,
                                            onRemoveSpendingClicked = viewModel::onRemoveSpendingClicked,
                                        )
                                    }
                                }
                            } else {
                                var beginOfPreviousMonth = beginOfCurrentMonth.minusMonths(1)
                                var allSpendingMinusPreviousMonth: List<Spending> =
                                    allSpendingValue.minus(allSpendingValueFilteredByLastMonth.toSet())
                                while (allSpendingMinusPreviousMonth.isNotEmpty()) {
                                    val beginOfMonth = beginOfPreviousMonth
                                    val allSpendingFilteredByLastMonth =
                                        allSpendingMinusPreviousMonth.filter { it.date.isAfter(beginOfPreviousMonth) }
                                    item { SummaryOfMonth(beginOfMonth, allSpendingFilteredByLastMonth) }
                                    beginOfPreviousMonth = beginOfPreviousMonth.minusMonths(1)
                                    allSpendingMinusPreviousMonth = allSpendingMinusPreviousMonth.minus(allSpendingFilteredByLastMonth.toSet())
                                }
                            }
                        }
                    }
                }
            }
        })
}

@Composable
fun TopBar(
    overviewType: OverviewType,
    onMonthlyComparisonTypeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTopAppBarDropdownMenuExpanded by remember { mutableStateOf(false) }
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
        },
        modifier = modifier,
    )
}

@Composable
fun SummaryOfMonth(beginOfCurrentMonth: LocalDateTime, allSpendingFilteredByMonth: List<Spending>) {
    val date = beginOfCurrentMonth.month.toString() + "." + beginOfCurrentMonth.year.toString()
    var amountByMonth = 0
    allSpendingFilteredByMonth.onEach { spending -> spending.spentAmount.let { amountByMonth += it } }
    Text(text = "amount spent $date == $amountByMonth")
    val categories = mutableSetOf<Category>()
    allSpendingFilteredByMonth.onEach { spending -> categories.add(spending.category) }
    when {
        categories.isEmpty() -> Text(text = "categories == empty")
        else -> {
            val amountsByCategories = StringBuffer()
            categories
                .sortedBy { it.name }
                .onEach { category ->
                    var amountByCategory = 0
                    allSpendingFilteredByMonth
                        .filter { it.category == category }
                        .onEach { spending -> spending.spentAmount.let { amountByCategory += it } }
                    if (category.name.isBlank()) {
                        amountsByCategories.append("empty category")
                    }
                    amountsByCategories.append("${category.name} == $amountByCategory, ")
                }
            Text(text = "$amountsByCategories")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpendingItem(
    spending: Spending,
    onSpendingClicked: (Spending) -> Unit,
    onSpendingLongClicked: (Spending, MutableState<Boolean>) -> Unit,
    isExpandedDropdownMenu: MutableState<Boolean> = remember { mutableStateOf(false) },
    onEditSpendingClicked: (LocalDateTime) -> Unit,
    onRemoveSpendingClicked: (String) -> Unit,
) {//
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .combinedClickable(
//                onClick = { onSpendingClicked(spending) },
                onClick = { onEditSpendingClicked(spending.date) }, // optional implementation
                onLongClick = { onSpendingLongClicked(spending, isExpandedDropdownMenu) },
                onDoubleClick = { onSpendingLongClicked(spending, isExpandedDropdownMenu) },
            ),
        elevation = 4.dp,
    ) {
        Row {
            val dateStr = spending.date.format(DateTimeFormatter.ofPattern(LOCALE_DATE_TIME_FORMATTER))
            Text(
                "date = $dateStr" +
                        "\nspentAmount = ${spending.spentAmount}" +
                        "\ncategory = ${spending.category.name}" +
                        "\nnote = ${spending.note}"
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
        ) {
            Box {
                DropdownMenu(
                    expanded = isExpandedDropdownMenu.value,
                    onDismissRequest = { isExpandedDropdownMenu.value = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            isExpandedDropdownMenu.value = false
                            onEditSpendingClicked(spending.date)
                        }
                    ) {
                        Text("Edit")
                    }
                    DropdownMenuItem(
                        onClick = {
                            isExpandedDropdownMenu.value = false
                            onRemoveSpendingClicked(spending.uuid)
                        }
                    ) {
                        Text("Remove")
                    }
                }
            }
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
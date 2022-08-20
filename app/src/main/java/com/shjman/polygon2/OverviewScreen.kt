package com.shjman.polygon2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import timber.log.Timber
import java.time.format.DateTimeFormatter

@Composable
fun OverviewScreen(
    lifecycleScope: LifecycleCoroutineScope,
    spentViewModel: SpentViewModel,
) {
    val onSelectedSpendingClicked = { spending: Spending -> Timber.d("clicked on == $spending") }
    val allSpending = remember { mutableStateOf(emptyList<Spending>()) }
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
        if (allSpending.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.Green)
            }
        } else {
            LazyColumn {
                items(
                    items = allSpending.value,
                    itemContent = {
                        SpendingItem(spending = it, selectedSpending = onSelectedSpendingClicked)
                    }
                )
            }
        }
    }
}

@Composable
fun SpendingItem(spending: Spending, selectedSpending: (Spending) -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { selectedSpending(spending) },
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
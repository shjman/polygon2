package com.shjman.polygon2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shjman.polygon2.data.Spending
import java.time.LocalDateTime

@Composable
fun EditSpendingScreen(
    localDateTime: LocalDateTime,
    editSpendingViewModel: EditSpendingViewModel,
    spending: MutableState<Spending?> = rememberSaveable { mutableStateOf(null) }
) {

    LaunchedEffect(Unit) {
            editSpendingViewModel.loadSpending(localDateTime)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit Spending screen")
        Row {
            Text(text = "date == ")
            Text(text = "xz")
        }
        Row {
            Text(text = "amount == ")
            Text(text = "99")
        }
    }
}
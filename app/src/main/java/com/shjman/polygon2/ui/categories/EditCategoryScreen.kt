package com.shjman.polygon2.ui.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditCategoryScreen(
    editCategoryViewModel: EditCategoryViewModel,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(1f),
            value = "category",
            onValueChange = {},
            maxLines = 4,
            placeholder = { Text("write note") },
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
        ) {
            Text(
                text = "done",
                color = Color.White,
            )
        }
    }
}